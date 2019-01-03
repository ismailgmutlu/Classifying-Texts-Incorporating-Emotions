import smart_open
from gensim.models import Word2Vec

class MySentences(object):
    def __init__(self, fname):
        self.fname = fname
        self.lis = [0]

    def __iter__(self):
        with smart_open.smart_open(self.fname, encoding='utf-8') as f:
            for uid, par in enumerate(f):
                par = par[2:-2]
                print(self.lis[0])
                self.lis[0] += 1
                yield par.split()
                
def train(train_corpus):
    model = Word2Vec(sentences=train_corpus, size=100, window=8, min_count=3, workers=4)
    return model

def train_save(train_corpus, filename):
    model = train(train_corpus)
    model.save(filename)

def load(filename):
    return Word2Vec.load(filename)

sentences = MySentences("/media/emre/hdd/bigfat/big.csv")
train_save(sentences, "/media/emre/hdd/bigfat/wordModelMoreIter")
