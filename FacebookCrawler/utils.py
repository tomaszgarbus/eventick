import facebook
import requests

ACCESS_TOKEN='EAAXugZATonpEBAF5JouWpjWiLAk1rwRuczNQD4qDebs86f3fuoviNfBgYoC7KwYmln9yzXya6aNAK2TMxzFr2nVgeAlwj8pSa2zsVpS7WXYdKDKjPrM9usnbwAazAkJFe9Lrbcvtp2bBA9PxUgwFHSI5Ep12zvbECv1qTMZCkx9YI3QXTfOb0MoOj2KZBHSIdX3xnnoki6w6W4CbtWc'

class GraphExtended(facebook.GraphAPI):
    def unfold_pagination(self, objects):
        ret = []
        while True:
            ret = ret + objects['data']
            if objects.get('paging', None) is not None and objects['paging'].get('next', None) is not None:
                objects = requests.get(objects['paging']['next']).json()
            else:
                break
        return ret

    def event_list_interested(self, event_id):
        interested = self.get_object(event_id+'/interested')
        return self.unfold_pagination(interested)

    def place_events(self, place_id):
        events = self.get_object(place_id+"/events")
        return self.unfold_pagination(events)

    def facebook_recommended_events(self):
        events = self.get_object('search?q=*&type=event')
        return self.unfold_pagination(events)

    def custom_query_list(self, query):
        objects = self.get_object(query)
        return self.unfold_pagination(objects)

def init(access_token):
    return GraphExtended(access_token)