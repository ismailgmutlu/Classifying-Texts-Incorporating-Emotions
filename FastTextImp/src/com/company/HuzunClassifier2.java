package com.company;

import com.beust.jcommander.Parameter;
import com.google.common.eventbus.Subscribe;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarStyle;
import org.antlr.v4.runtime.Token;
import zemberek.apps.ConsoleApp;
import zemberek.classification.FastTextClassifier;
import zemberek.classification.FastTextClassifierTrainer;
import zemberek.classification.FastTextClassifierTrainer.LossType;
import zemberek.core.ScoredItem;
import zemberek.core.embeddings.FastText;
import zemberek.core.embeddings.FastTextTrainer;
import zemberek.core.embeddings.WordVectorsTrainer;
import zemberek.morphology.analysis.SentenceAnalysis;
import zemberek.normalization.TurkishSentenceNormalizer;
import zemberek.tokenization.*;
import zemberek.morphology.TurkishMorphology;
import zemberek.morphology.analysis.SingleAnalysis;
import zemberek.morphology.analysis.WordAnalysis;
import zemberek.tokenization.antlr.TurkishLexer;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HuzunClassifier2 extends ConsoleApp{

    @Parameter(names = {"--sentence", "-s"}, description = "Give your sentence to learn which emotions it includes.")
    String sentence = "Bugün okuldan dönmek istemedim.";


    @Override
    public String description() {
        return "Hüzün Working";
    }

    @Override
    public void run() throws IOException{



        Path modelPath = Paths.get("labeledtexts2.model");
        System.out.println(modelPath.toAbsolutePath());

        TurkishMorphology morphology = TurkishMorphology.createWithDefaults();
        Path lookupRoot = Paths.get("normalization");
        Path lmPath = Paths.get("lm/lm.2gram.slm");
        TurkishSentenceNormalizer normalizer = new TurkishSentenceNormalizer(morphology, lookupRoot, lmPath);

        List<String> avoid = readAvoidWords("kelimeler.txt");

        String lemmatized = convert(sentence, morphology, normalizer, avoid);

        // System.out.println(lemmatized);

        try {
            // FastTextClassifier classifier2 = FastTextClassifier.load(output);
            FastTextClassifier classifier;
            classifier = FastTextClassifier.load(modelPath);

            System.out.println("Lemmatized : " + lemmatized);

            List<ScoredItem<String>> res = classifier.predict(lemmatized, 1);
            // System.out.println(res.size());

            for (ScoredItem<String> re : res) {
                String out = re.toString().substring(9);
                out = out.split(" ")[0];
                if(out.equals("uzuntu")){
                    out = "huzun";
                }
                System.out.println("Duygu : " + out);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }




    }

    public static String convert(String inputPar, TurkishMorphology morphology,
                                 TurkishSentenceNormalizer norm, List<String> avoid){

        TurkishSentenceExtractor extractor = TurkishSentenceExtractor.DEFAULT;
        TurkishTokenizer tokenizer = TurkishTokenizer.DEFAULT;

        StringBuilder sum = new StringBuilder();
        List<String> sentences = extractor.fromParagraph(inputPar);
        for (String sentence: sentences) {
            String normalized = norm.normalize(sentence);
            SentenceAnalysis analysis = morphology.analyzeAndDisambiguate(normalized);

            for (SingleAnalysis single: analysis.bestAnalysis())
                sum.append(" ").append(single.getLemmas().get(0));
        }

        List<Token> tokens = tokenizer.tokenize(sum.toString());
        sum = new StringBuilder();
        for (Token tok: tokens) {
            if (TurkishLexer.VOCABULARY.getDisplayName(tok.getType()).equals("Word") &&
                    !avoid.contains(tok.getText()) && !tok.getText().equalsIgnoreCase("UNK"))
                sum.append(" ").append(tok.getText());
        }

        return sum.toString();
    }

    public static List<String> readAvoidWords(String filename) {

        // This will reference one line at a time
        ArrayList<String> kelimeler = new ArrayList<String>();

        String line = null;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader =
                    new FileReader(filename);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {
                kelimeler.add(line);
            }

            // Always close files.
            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            filename + "'");
        } catch (IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + filename + "'");
        }
        return kelimeler;

    }



    public static void main(String[] args) {
        new HuzunClassifier2().execute(args);
    }
}
