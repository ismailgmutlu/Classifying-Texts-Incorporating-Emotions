Classifying Texts Incorporating Emotions
====================================================

Text Classification
Copyright (c) 2018, Koc University and/or its affiliates. All rights reserved.

Requirements
============

Use Python 3.6 for tensorflow and keras libraries

Documentation & Examples
========================

For creating a classification model, we first need a training set. For LSTM, training data is directly collected from the database. There is no preprocessing done in the code itself, since preprocessed data is stored in the database and directly used.

Training can be done by running the script huzun-keras-imp.py.

If you want to play with the parameters of the training you need to change it in the code. In this version following parameters are used with 80-20 validation split:

- Maximum sequence length : 5.
- Epochs : 50
- Batch Size : 128

If you just want to predict, you can run lstm-loader.py as following:

python lstm-loader.py -i "sentence" (within quotes)

After some execution time (10-20 seconds) this will return the emotion. Keep in mind that you need the following files and folders to be able to predict:

- lm
- normalization
- kelimeler.txt

This files serve the purpose of preprocessing for test data.




