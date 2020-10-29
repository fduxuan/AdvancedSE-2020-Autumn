from flask import Flask
from blueprint.meeting import meeting_blueprint
from blueprint.user import user_blueprint
from blueprint.paper import paper_blueprint
from middleware.config import Config


app = Flask(__name__)
app.config.from_object(Config)
app.register_blueprint(meeting_blueprint)
app.register_blueprint(user_blueprint)
app.register_blueprint(paper_blueprint)

if __name__ == '__main__':
    app.run(host=app.config.get('HOST'), port=app.config.get('PORT'))
