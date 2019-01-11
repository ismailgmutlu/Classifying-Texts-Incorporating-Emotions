# Classifying Texts Incorporating Emotions
Text Classification Copyright (c) 2018, Koc University and/or its affiliates. All rights reserved.
# Requirements
Python3 (version 3.5+)
gensim (https://radimrehurek.com/gensim/)
# Documenatation
Word2Vec is a word embedding algorithm implementation from gensim library. In the pipeline of this project, it is used as a feature extractor that feeds into the LSTM classifier. 

Word Embedding is an unsupervised algorithm that maps words into the vector space based on their meanings. These meanings are derived from the context of this word in the corpus. Therefore every instance of the same word in the corpus contributes to its meaning, therefore its vector representation. The surrounding context of a word is derived from a window of other words surrounding it. Vector representatins of all words in the corpus is learned and imporved iteratively. The resulting mappings map words with similar meanings closer in the vector space. More interestingly, but irrelevant for this project, is that vector arithmethic on the generated vectors preserve meaning. For instance, following the most known example, 'kral' - 'erkek' + 'kadın' = 'kraliçe' ('king' - 'man' + 'woman' = 'queen'). 

For more information on Word2Vec: https://papers.nips.cc/paper/5021-distributed-representations-of-words-and-phrases-and-their-compositionality.pdf

Doc2Vec is an extenion of Word2Vec, in that the generated word vector depends also on the paragraph it is in. However, we have decided to utilize Word2Vec instead, since training Doc2Vec is a more fragile approach, that requires either a huge corpus, or a well maintained one.

The Word2Vec.py module is fairly straightforward to use. The train function hardcodes all hyperparameters to the values that gave the best results. 

Only imprtant thing to consider is that the corpus must be generated via the MySentences class. MySentences takes the corpus filename as an input and returns a multiple use iterator that iterates over the corpus. The iterator must allow multiple iterations over it, since Word2Vec iterates over the corpus multiple times.
