Classifying Texts Incorporating Emotions
====================================================

Text Classification
Copyright (c) 2018, Koc University and/or its affiliates. All rights reserved.

Requirements
============

Java (version >= 7.0.0)

Documentation & Examples
========================

For creating a classification model, we first need a training set. A file that contains documents and their labels. Training data should be prepared FastText style. For example, a training set that contains paragraphs and their emotions.

__label__sadness Ah bu ülkenin hali beni çok üzüyor.
__label__happy Galatasaray bu sene yine şampiyon.    
__label__fear Bu sınavın sonucu beni çok korkutuyor. 

Each line must contain a document and its label(s). A document can be a sentence, or a paragraph. Algorithm should work with a page long document but performance may be lower than expected. A label must have a "__label__" prefix.

However, it is usually suggested to preprocess the training set. You can also find lemmatization, removal of stop words and removal of some punctuations in the code.

Training can be done with a console application or with the API. Using console application is easy. You can run the project with jar as below:

java -jar huzunProject.jar -s "sentence" (within quotes)

After you enter the sentence, output may look like this:

__label__happy : 0.000010
__label__surprise : -11.483298
__label__sadness : -11.512561

As mentioned before, classification algorithm is based on a port of fastText project. Please refer to the project documentation and related scientific papers for more information: 

https://fasttext.cc



