from keras.models import Sequential
from keras.layers import Dense, Dropout, LSTM

import mysql.connector
import numpy as np
from keras.utils import to_categorical

from mysql.connector import Error

from gensim.test.utils import get_tmpfile
from gensim.models import Word2Vec

texts = []  # list of text samples
labels_index = {"huzun": 0,
                "mutluluk": 1,
                "ofke": 2,
                "korku": 3,
                "sasirma": 4,
                "tiksinme": 5}  # dictionary mapping label name to numeric id
labels = []  # list of label ids

par_limit = 4500

try:
   mySQLconnection = mysql.connector.connect(host='huzuninstance.chezocpbjx4z.eu-central-1.rds.amazonaws.com',
                             database='masterhuzun',
                             user='masterhuzun',
                             password='123456789Ai!')
   sql_select_Query = "select lemmatized_v4.paragraph, huzun, melankoli, uzuntu, mutluluk, ofke, korku, sasirma, tiksinme, diger from parsed_data_v3 JOIN lemmatized_v4 where parsed_data_v3.label_count > 0 AND parsed_data_v3.p_id = lemmatized_v4.p_id"
   cursor = mySQLconnection .cursor()
   cursor.execute(sql_select_Query)
   records = cursor.fetchall()

   counter_huzun = 0
   counter_mutluluk = 0
   counter_ofke = 0
   counter_korku = 0
   counter_sasirma = 0
   counter_tiksinme = 0

   for row in records:
       paragraph = row[0]
       huzun = row[1]
       melankoli = row[2]
       uzuntu = row[3]
       mutluluk = row[4]
       ofke = row[5]
       korku = row[6]
       sasirma = row[7]
       tiksinme = row[8]
       diger = row[9]
       if (huzun > 0 or melankoli > 0 or uzuntu > 0) and counter_huzun <= par_limit:
           texts.append(paragraph)
           labels.append(labels_index["huzun"])
           counter_huzun += 1
       if (mutluluk > 0) and counter_mutluluk <= par_limit:
           texts.append(paragraph)
           labels.append(labels_index["mutluluk"])
           counter_mutluluk += 1
       if (ofke > 0) and counter_ofke <= par_limit:
           texts.append(paragraph)
           labels.append(labels_index["ofke"])
           counter_ofke += 1
       if (korku > 0) and counter_korku <= par_limit:
           texts.append(paragraph)
           labels.append(labels_index["korku"])
           counter_korku += 1
       if (sasirma > 0) and counter_sasirma <= par_limit:
           texts.append(paragraph)
           labels.append(labels_index["sasirma"])
           counter_sasirma += 1
       if (tiksinme > 0) and counter_tiksinme <= par_limit:
           texts.append(paragraph)
           labels.append(labels_index["tiksinme"])
           counter_tiksinme += 1

   cursor.close()

except Error as e :
    print ("Error while connecting to MySQL", e)
finally:
    #closing database connection.
    if(mySQLconnection .is_connected()):
        mySQLconnection.close()
        print("MySQL connection is closed")

print('Found %s texts.' % len(texts))

from keras.preprocessing.text import Tokenizer
from keras.preprocessing.sequence import pad_sequences

MAX_NB_WORDS = 40000
MAX_SEQUENCE_LENGTH = 5

tokenizer = Tokenizer(nb_words=MAX_NB_WORDS)
tokenizer.fit_on_texts(texts)
sequences = tokenizer.texts_to_sequences(texts)


word_index = tokenizer.word_index
print('Found %s unique tokens.' % len(word_index))

data = pad_sequences(sequences, maxlen=MAX_SEQUENCE_LENGTH)

labels = to_categorical(np.asarray(labels))
print('Shape of data tensor:', data.shape)
print('Shape of label tensor:', labels.shape)

VALIDATION_SPLIT = 0.2

indices = np.arange(data.shape[0])
np.random.shuffle(indices)
data = data[indices]
labels = labels[indices]
nb_validation_samples = int(VALIDATION_SPLIT * data.shape[0])

x_train = data[:-nb_validation_samples]
y_train = labels[:-nb_validation_samples]
x_val = data[-nb_validation_samples:]
y_val = labels[-nb_validation_samples:]


fname = get_tmpfile("wordModel")
w2vModel = Word2Vec.load(fname)

embedding_layer = w2vModel.wv.get_keras_embedding()

model = Sequential()
model.add(embedding_layer)
model.add(Dropout(0.2))
model.add(LSTM(100, dropout=0.2, recurrent_dropout=0.2))
model.add(Dense(250, activation='relu'))
model.add(Dropout(0.2))
model.add(Dense(len(labels_index), activation='sigmoid'))
model.compile(loss='categorical_crossentropy', optimizer='rmsprop', metrics=['categorical_accuracy'])

print(len(x_train))

# happy learning!
model.fit(x_train, y_train, validation_data=(x_val, y_val),
          epochs=50, batch_size=128)

print('\nAccuracy: {}'. format(model.evaluate(x_val, y_val)[1]))

# serialize model to JSON
model_json = model.to_json()
with open("model.json", "w") as json_file:
    json_file.write(model_json)
# serialize weights to HDF5
model.save_weights("model.h5")
print("Saved model to disk")