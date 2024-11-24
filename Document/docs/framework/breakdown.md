# **业务拆分思路**

我们将当前的StarChair项目拆分为6个微服务

**其名称与功能如下**

| 服务名称                    | 服务功能                                                   |
| --------------------------- | ---------------------------------------------------------- |
| User Metadata Service       | 该服务用于管理用户的详细数据                               |
| Conference Metadata Service | 该服务用于管理会议的详细数据                               |
| Draft Metadata Service      | 该服务用于管理稿件的详细数据                               |
| Invitation Service          | 该服务用于管理对PCmember的邀请信息                         |
| Discussion Service          | 该服务用于管理对于一个稿件的讨论内容                       |
| Review Process Service      | 该服务用于管理会议的审稿流程，是整个会议评审系统的核心服务 |
| Notification Service        | 异步消息队列和系统通知服务                                 |

下面将对我们的数据库拆分思路进行详细分析



----



## 1. 基于"元数据"的静态数据拆分

### 1.1 元数据与详细数据

!!! info
    元数据（Metadata），又称中介数据、中继数据，为描述数据的数据（data about data），主要是描述数据属性（property）的信息，用来支持如指示存储位置、历史数据、资源查找、文件记录等功能。



在我们的系统中，存在着很多 **<u>【实体】</u>**，比如用户、会议、稿件等，每个**【实体】**都有着自己的详细信息，比如稿件会有`名称`、`作者`、`文件路径`等。在整个审稿流程中，实体与实体之间存在着各种的联系，比如用户在会议中担任某种角色，稿件被投递给会议、并经由用户审核等等。这时候我们通常会使用 ==元数据== 的形式来描述实体与实体之间的联系。

**举一个例子：**

* 在关系型数据库中，我们使用外键描述联系，在保证约束的同时使用join等方式使用 **【元数据】** 进行查表，从而获取相关联实体的 **【详细信息】**。

* 而在我们审稿系统的设计过程中，很多情况下我们同样只需要一个 **【元数据】** 标记实体即可，并不需要将实体的 **【详细信息】** 储存在同一个位置。这种设计可以保证在详细数据的数据库不断增长时，相关联的数据库由于只保存了 **【元数据】** ，<u>所以并不会产生相应的巨大增长。</u>



-----



### 1.2 如何界定一个实体的详细数据

!!! hint
    上面的划分是非常直接而容易理解的，但在实际的场景中，如何确定一个数据的详细数据有哪些属性呢？

比如在我们的审稿系统中，`评分` 应不应该算作`稿件`这个实体的一个详细信息呢？我们通过什么方式判定一个属性应不应当算作**【实体】**的**【详细信息】**呢？



**在本次划分任务中，我们主要的评判方式是:**

> 这一个属性对于**【实体】**来说是不是**【静态的、固有的】**，<u>换句话说，这个属性在这个实体中是否会经常性的改变。</u>
>
> * 如果 ***不是*** ，那么那么他应该作为一个实体的**【详细数据】**进行保存；
> * 如果 ***是*** ，那么应该由一个单独的**【关系描述】**去刻画他，而不是作为一个详细数据进行保存。

由此，对于上面的 `评分应不应该算作稿件这个实体的一个详细信息呢?` 这个问题，我们给出的答案是**否**。

* 因为对于一个稿件来说他的评分在审稿过程中会被多次更改，所以他并不是静态的，不是这个稿件的**【详细属性】**；
* 而`文件路径`、`名称`、`作者`，在稿件创建后的改动情况会非常的少，那么我们将这些属性作为稿件的**【详细属性】**进行储存。

基于以上的原则，我们对于`User（用户）`,` Conference（会议）`, `Draft（稿件）` 三个主要的实体，分别提取了 **User Metadata Service**、**Conference Metadata Service**、**Draft Metadata Service** 作为这三个实体的详细数据的 ==保存服务== 

而**Review Process Service** 就是我们所属的 **【关系描述】**，这个服务主要用于储存关系中的一些属性，比如`评分`和`rebuttal`等。而最后三个服务则是<u> *根据业务的独立性* </u>进行拆分的，这些数据库的独立出现是显而易见的。



------



### 1.3 为什么需要基于"元数据"进行拆分

我们可以举一个例子，当一个会议的稿件的规模达到 <u>10亿级别</u> 的时候，储存draft信息的数据库将会巨大。

那么我们一定不会选择使用单点架构去进行存储，而是进行**分布式**的储存，比如存储在100台机器的几十T的磁盘中。而这种储存方式就会带来一个问题，如果我们将`评分`视为文件的一个**【详细信息】**，我们就需要在巨大的上百T级别的数据库进行更改和查询，这无疑是有性能问题和安全风险的。

​	而如果我们只储存**【静态数据】**在稿件中，而把动态的经常使用的数据进行提取的话，那描述`评分`这个属性的条目大小将会大大降低。因为不需要去额外储存巨大体量的稿件的详细信息，只需要储存他的**【元数据】**集合，可能一台机器就可以储存完毕。那么我们在进行评分修改的时候，我们完全不需要知道这个稿件的名称、作者、文件路径是什么，只需要在**【描述元数据之间联系的关系描述】**也就是我们的 **Review Process Service** 服务中进行更改即可。

也就是说，分离**【详细数据】**与**【元数据】**可以让我们对【数据本身】和【数据之间的联系】分离开来，从而使整个架构更具扩展性，充分展示了微服务架构对分布式节点的利用。

另一个值得思考的问题是 ==缓存== 的使用。虽然在我们的项目架构中并不会用到缓存，但缓存被大量用于实际的业务场景之中。我们基于元数据的分离方法可以很好的使用缓存，也就是说，**【详细数据】**可以被放在缓存中进行高速的查找，从而极大的缩短服务间调用的耗时。

-------



### 1.4 缺陷

世界上并不存在一个完整的系统，就像CAP永远无法同时满足一样。这种基于元数据的设计必将涉及到比较多场景下的服务间调用，但在通常情况下这是无法避免的、任何系统都会出现的问题。当然，我们可以稍微牺牲一下数据冗余和缓存来加快某些具体的业务的速度，以及进行许多其他方面的细节优化。

-----------



## 2. 总结

我们本次的业务拆分并不基于**【数据库】**，也不基于**【业务】**，而是综合考虑了业务、数据的一种基于**【元数据】**的拆分方法。这种拆分的目的主要是希望我们的系统拥有更高的可扩展性（Scalability），这种拆分方式只能说是我们的一种尝试，我们也希望能在实现和使用的过程中发现这种设计的优缺点，以供大家讨论。

