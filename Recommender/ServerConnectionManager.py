from requests.auth import HTTPBasicAuth

SERVER_NAME="localhost:8080"

def authenticate(userId, accessToken):
    return requests.get('https://' + SERVER_NAME + '/login', auth=(userId, accessToken))
