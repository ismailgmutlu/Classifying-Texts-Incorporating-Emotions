import smart_open
from gensim.models.doc2vec import Doc2Vec, TaggedDocument 

class MySentences(object):
    def __init__(self, fname, lis, tok=False):
        self.lis = lis
        self.fname = fname
        self.tokens_only = tok

    def __iter__(self):
        with smart_open.smart_open(self.fname, encoding='utf-8') as f:
            for uid, par in enumerate(f):
                par = par[2:-2]
                print(self.lis[0])
                if self.tokens_only:
                    yield par.split()
                else:
                    self.lis[0] = self.lis[0] + 1
                    yield TaggedDocument(par.split(), [uid])
        
def model_init():
    model = Doc2Vec(vector_size=250, min_count=5, epochs=20, workers=4)
    return model

def train_save(model, train_corpus, filename):
    train_model(model, train_corpus)
    save_model(model, filename)

def train_model(model, train_corpus):
    model.build_vocab(train_corpus)
    model.train(train_corpus, total_examples=model.corpus_count, epochs=model.epochs)

def save_model(model, filename):
    model.save(filename)

def load_model(filename):
    model = Doc2Vec.load(filename)
    return model

dumb = [0]
sentences = MySentences("/media/emre/hdd/bigfat/big.csv", dumb)
model = model_init()
train_save(model, sentences, "fatmodel")
