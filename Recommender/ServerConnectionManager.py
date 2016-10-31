import requests

SERVER_NAME="localhost:8080"

def authenticate(userId, accessToken):
    return requests.get('http://' + SERVER_NAME + '/events/all', auth=(userId, accessToken))

