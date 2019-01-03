import getopt
import sys

from keras.models import model_from_json
from keras.preprocessing.text import Tokenizer
from keras_preprocessing.sequence import pad_sequences
from subprocess import Popen, PIPE, STDOUT

def main(argv):
    # load json and create model
    input_text = ''

    try:
        opts, args = getopt.getopt(argv, "hi:", ["input="])
    except getopt.GetoptError:
        print
        'lstm-loader.py -i <inputtext>'
        sys.exit(2)
    for opt, arg in opts:
        if opt == '-h':
            print
            'lstm-loader.py -i <inputtext>'
            sys.exit()
        elif opt in ("-i", "--input"):
            input_text = arg

    json_file = open('model.json', 'r')
    loaded_model_json = json_file.read()
    json_file.close()
    loaded_model = model_from_json(loaded_model_json)
    # load weights into new model
    loaded_model.load_weights("model.h5")
    print("Loaded model from disk")

    texts = []  # list of text samples
    texts.append(input_text)
    labels_index = {0: "huzun",
                    1: "mutluluk",
                    2: "ofke",
                    3: "korku",
                    4: "sasirma",
                    5: "tiksinme"}  # dictionary mapping label name to numeric id

    MAX_NB_WORDS = 40000
    MAX_SEQUENCE_LENGTH = 5

    lemmatized_texts = []
    for text in texts:
        p = Popen(['java', '-jar', 'lemmatizer.jar', '--input', text], stdout=PIPE, stderr=STDOUT)
        for line in p.stdout:
            line = line.decode('utf-8')
            print(line)
            if line[:3] == 'xyz':
                lemmatized_texts.append(line[4:len(line) - 2])

    print(lemmatized_texts)

    tokenizer = Tokenizer(nb_words=MAX_NB_WORDS)
    tokenizer.fit_on_texts(lemmatized_texts)
    sequences = tokenizer.texts_to_sequences(lemmatized_texts)

    word_index = tokenizer.word_index
    print('Found %s unique tokens.' % len(word_index))

    data = pad_sequences(sequences, maxlen=MAX_SEQUENCE_LENGTH)
    print('Shape of data tensor:', data.shape)

    result = loaded_model.predict(data, verbose=0)

    max_label = max(result[0])
    index = 0

    for i, res in enumerate(result[0], 0):
        if max_label == res:
            index = i
            break
        i += 1

    label = labels_index[index]
    print("Duygu: "+label)
    return label

if __name__ == "__main__":
   main(sys.argv[1:])


