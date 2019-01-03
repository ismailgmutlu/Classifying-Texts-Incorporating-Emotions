package com.company;

import com.beust.jcommander.Parameter;
import zemberek.classification.FastTextClassifier;
import zemberek.core.ScoredItem;
import zemberek.core.embeddings.FastTextTrainer;
import zemberek.morphology.TurkishMorphology;
import zemberek.morphology.analysis.SingleAnalysis;
import zemberek.morphology.analysis.WordAnalysis;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import java.io.IOException;
import java.nio.file.Paths;
import zemberek.classification.FastTextClassifierTrainer;
import zemberek.core.embeddings.FastText;
import zemberek.core.logging.Log;
import com.google.common.eventbus.Subscribe;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarStyle;
import zemberek.apps.ConsoleApp;
import zemberek.core.embeddings.WordVectorsTrainer;

import zemberek.classification.FastTextClassifierTrainer.LossType;

public class HuzunClassifier extends ConsoleApp{

    @Parameter(names = {"--wordNGrams", "-wng"},
            description = "Word N-Gram order.")
    int wordNGrams = WordVectorsTrainer.DEFAULT_WORD_NGRAM;

    @Parameter(names = {"--dimension", "-dim"},
            description = "Vector dimension.")
    int dimension = WordVectorsTrainer.DEFAULT_DIMENSION;

    @Parameter(names = {"--contextWindowSize", "-ws"},
            description = "Context window size.")
    int contextWindowSize = WordVectorsTrainer.DEFAULT_CONTEXT_WINDOW_SIZE;

    @Parameter(names = {"--threadCount", "-tc"},
            description = "Thread Count.")
    int threadCount = WordVectorsTrainer.DEFAULT_TC;

    @Parameter(names = {"--minWordCount", "-minc"},
            description = "Words with lower than this count will be ignored..")
    int minWordCount = WordVectorsTrainer.DEFAULT_MIN_WORD_COUNT;

    ProgressBar progressBar;

    @Parameter(names = {"--input", "-i"},
            description = "Classifier training file. each line should contain a single document and "
                    + "one or more class labels. "
                    + "Document class label needs to have __label__ prefix attached to it.")
    Path input = FileSystems.getDefault().getPath("", "labeledtexts.txt");

    @Parameter(names = {"--output", "-o"},
            description = "Output model file.")
    Path output = FileSystems.getDefault().getPath("", "labeledtexts.model");

    @Parameter(names = {"--lossType", "-l"},
            description = "Model type.")
    LossType lossType = LossType.SOFTMAX;

    @Parameter(names = {"--applyQuantization", "-q"},
            description = "If used, applies quantization to model. This way model files will be "
                    + " smaller. Underlying algorithm uses 8 bit values for weights instead of 32 bit floats."
                    + " Quantized model will be saved same place of output with name [output].q ")
    boolean applyQuantization = false;

    @Parameter(names = {"--cutOff", "-c"},
            description = "Reduces dictionary size with given threshold value. "
                    + "Dictionary entries are sorted with l2-norm values and top `cutOff` are selected. "
                    + "This greatly reduces model size. This option is only available if"
                    + "applyQuantization flag is used.")
    int cutOff = -1;

    @Parameter(names = {"--epochCount", "-ec"},
            description = "Epoch Count.")
    int epochCount = FastTextClassifierTrainer.DEFAULT_EPOCH;

    @Parameter(names = {"--learningRate", "-lr"},
            description = "Learning rate. Should be between 0.01-2.0")
    float learningRate = FastTextClassifierTrainer.DEFAULT_LR;

    @Parameter(names = {"--update", "-u"}, description = "0 or 1. If you want to retrain the model use 1, 0 default.")
    int update = 0;

    @Parameter(names = {"--testText"}, description = "Path to the file containing the text that needs testing")
    Path testFile = FileSystems.getDefault().getPath("", "testText.txt");

    @Parameter(names = {"--unnecessaryText"}, description = "Path to the file containing less meaningful words")
    Path unnecessaryWords = FileSystems.getDefault().getPath("", "unnecessaryWords.txt");

    @Subscribe
    public void trainingProgress(FastTextTrainer.Progress progress) {

        synchronized (this) {
            if (progressBar == null) {
                System.setProperty("org.jline.terminal.dumb", "true");
                progressBar = new ProgressBar("", progress.total, ProgressBarStyle.ASCII);
            }
        }
        progressBar.stepTo(progress.current);
        progressBar.setExtraMessage(String.format("lr: %.6f", progress.learningRate));
    }

    @Override
    public String description() {
        return "Generates a text classification model from a training set. Classification algorithm"
                + " is based on Java port of fastText library. It is usually suggested to apply "
                + "tokenization, lower-casing and other specific text operations to the training set"
                + " before training the model. "
                + "Algorithm may be more suitable for sentence and short paragraph"
                + " level texts rather than long documents.\n "
                + "In the training set, each line should contain a single document. Document class "
                + "label needs to have __label__ prefix attached to it. Such as "
                + "[__label__sports Match ended in a draw.]\n"
                + "Each line (document) may contain more than one label.\n"
                + "If there are a lot of labels, LossType can be chosen `HIERARCHICAL_SOFTMAX`. "
                + "This way training and runtime speed will be faster with a small accuracy loss.\n "
                + "For generating compact models, use -applyQuantization and -cutOff [dictionary-cut-off] "
                + "parameters.";
    }

    @Override
    public void run() throws IOException{

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your MySQL JDBC Driver?");
            e.printStackTrace();
            return;
        }

        System.out.println("MySQL JDBC Driver Registered!");
        Connection connection = null;

        try {
            connection = DriverManager.
                    getConnection("jdbc:mysql://" + "huzuninstance.chezocpbjx4z.eu-central-1.rds.amazonaws.com" + ":" + "3306" + "/" + "masterhuzun?useUnicode=yes&characterEncoding=UTF-8", "masterhuzun", "123456789Ai!");
        } catch (SQLException e) {
            System.out.println("Connection Failed!:\n" + e.getMessage());
        }

        if (connection != null) {
            System.out.println("SUCCESS!!!! I made it!");
        } else {
            System.out.println("FAILURE! Failed to make connection!");
        }

        if (update != 0){

            String query = "SELECT p_id, paragraph FROM masterhuzun.parsed_data_v3 where label_count > 0\n";

            String insertQuery = "INSERT INTO lemmatized_v3 (p_id, paragraph) VALUES (?, ?)" ;



            try {
                Statement st = connection.createStatement();

                ResultSet rs = st.executeQuery(query);

                PreparedStatement insertSt = connection.prepareStatement(insertQuery);

                while (rs.next())
                {
                    int id = rs.getInt("p_id");
                    String paragraph = rs.getString("paragraph");
                    //paragraph = convert(paragraph);
                    // print the results
                    paragraph = convert(paragraph, unnecessaryWords.toAbsolutePath().toString());
                    insertSt.setInt(1, id);
                    insertSt.setString(2, paragraph);
                    insertSt.addBatch();
                }

                insertSt.executeBatch();
                insertSt.close();
                st.close();
            }
            catch (Exception e)
            {
                System.err.println("Got an exception! ");
                System.err.println(e.getMessage());
            }
        }

        String parsedDataQuery = "select lemmatized_v4.paragraph, lemmatized_v4.p_id, huzun, melankoli, uzuntu, mutluluk, ofke, diger, korku, sasirma, tiksinme from lemmatized_v4 JOIN parsed_data_v3 where label_count > 0 AND lemmatized_v4.p_id = parsed_data_v3.p_id";

        try {
            Statement stTrain = connection.createStatement();
            ResultSet rs = stTrain.executeQuery(parsedDataQuery);

            BufferedWriter writer = new BufferedWriter(new FileWriter(input.toAbsolutePath().toString()));

            Writer testWriter = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("testText.txt"), "utf-8"));

            String prefix = "__label__";

            int digerCount = 0;
            int testPid = 910;

            while (rs.next())
            {
                String paragraph = rs.getString("lemmatized_v4.paragraph");

                int pid = rs.getInt("p_id");
                ArrayList<Integer> values = new ArrayList<>();
                int huzun = rs.getInt("huzun");
                values.add(huzun);
                int melankoli = rs.getInt("melankoli");
                values.add(melankoli);
                int uzuntu = rs.getInt("uzuntu");
                values.add(uzuntu);
                int mutluluk = rs.getInt("mutluluk");
                values.add(mutluluk);
                int tiksinme = rs.getInt("tiksinme");
                values.add(tiksinme);
                int korku = rs.getInt("korku");
                values.add(korku);
                int ofke = rs.getInt("ofke");
                values.add(ofke);
                int sasirma = rs.getInt("sasirma");
                values.add(sasirma);
                int diger = rs.getInt("diger");
                values.add(diger);
                int maximum = Collections.max(values);

                String label = "Diger";
                int digerVar = 0;



                if (maximum == huzun){
                    label = "uzuntu";
                    digerVar = 1;
                    if(pid > testPid && pid < 1000){
                        String line = pid + " " + label + " " + paragraph + "\r\n";
                        testWriter.write(line);
                    }
                    else{
                        String line = prefix + label + " " + paragraph + "\r\n";
                        writer.write(line);
                    }
                }
                if (maximum == melankoli){
                    label = "uzuntu";
                    digerVar = 1;
                    if(pid > testPid  && pid < 1000){
                        String line = pid + " " + label + " " + paragraph + "\r\n";
                        testWriter.write(line);
                    }
                    else{
                        String line = prefix + label + " " + paragraph + "\r\n";
                        writer.write(line);
                    }
                }
                if (maximum == sasirma){
                    label = "sasirma";
                    digerVar = 1;
                    if(pid > testPid && pid < 1000){
                        String line = pid + " " + label + " " + paragraph + "\r\n";
                        testWriter.write(line);
                    }
                    else{
                        String line = prefix + label + " " + paragraph + "\r\n";
                        writer.write(line);
                    }
                }
                if (maximum == ofke){
                    label = "ofke";
                    digerVar = 1;
                    if(pid > testPid && pid < 1000){
                        String line = pid + " " + label + " " + paragraph + "\r\n";
                        testWriter.write(line);
                    }
                    else{
                        String line = prefix + label + " " + paragraph + "\r\n";
                        writer.write(line);
                    }
                }
                if (maximum == korku){
                    label = "korku";
                    digerVar = 1;
                    if(pid > testPid && pid < 1000){
                        String line = pid + " " + label + " " + paragraph + "\r\n";
                        testWriter.write(line);
                    }
                    else{
                        String line = prefix + label + " " + paragraph + "\r\n";
                        writer.write(line);
                    }
                }
                if (maximum == tiksinme){
                    label = "tiksinme";
                    digerVar = 1;
                    if(pid > testPid && pid < 1000){
                        String line = pid + " " + label + " " + paragraph + "\r\n";
                        testWriter.write(line);
                    }
                    else{
                        String line = prefix + label + " " + paragraph + "\r\n";
                        writer.write(line);
                    }
                }
                if (maximum == uzuntu){
                    label = "uzuntu";
                    digerVar = 1;
                    if(pid > testPid && pid < 1000){
                        String line = pid + " " + label + " " + paragraph + "\r\n";
                        testWriter.write(line);
                    }
                    else{
                        String line = prefix + label + " " + paragraph + "\r\n";
                        writer.write(line);
                    }
                }
                if (maximum == mutluluk){
                    label = "mutluluk";
                    digerVar = 1;
                    if(pid > testPid && pid < 1000){
                        String line = pid + " " + label + " " + paragraph + "\r\n";
                        testWriter.write(line);
                    }
                    else{
                        String line = prefix + label + " " + paragraph + "\r\n";
                        writer.write(line);
                    }
                }


                if (maximum == diger){
                    if(digerVar == 0){
                        label = "diger";
                        if(pid > testPid && pid < 1000){
                            //String line = pid + " " + label + " " + paragraph + "\r\n";
                            //testWriter.write(line);
                        }
                        else{
                            digerCount++;
                            if(digerCount > 0){
                                System.out.println("diger sınırda.");
                            }else{
                                String line = prefix + label + " " + paragraph + "\r\n";
                                writer.write(line);
                            }

                        }
                    }
                }

            }
            testWriter.close();
            writer.close();
            stTrain.close();
            connection.close();
        }
        catch (Exception e)
        {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
        }

        Log.info("Generating classification model from %s", input);

        FastTextClassifierTrainer trainer = FastTextClassifierTrainer.builder()
                .epochCount(epochCount)
                .learningRate(learningRate)
                .lossType(lossType)
                .quantizationCutOff(cutOff)
                .minWordCount(minWordCount)
                .threadCount(threadCount)
                .wordNgramOrder(wordNGrams)
                .dimension(dimension)
                .contextWindowSize(contextWindowSize)
                .build();

        Log.info("Training Started.");
        trainer.getEventBus().register(this);

        byte ptext[] = input.toAbsolutePath().toString().getBytes();

        FastTextClassifier classifier = trainer.train(Paths.get(new String(ptext, "UTF-8")));



        if (progressBar != null) {
            progressBar.close();
        }

        Log.info("Saving classification model to %s", output);
        FastText fastText = classifier.getFastText();
        Path path = Paths.get("labeledtexts22.model");
        fastText.saveModel(output);

        if (applyQuantization) {
            Log.info("Applying quantization.");
            if (cutOff > 0) {
                Log.info("Quantization dictionary cut-off value = %d", cutOff);
            }
            Path parent = output.getParent();
            String name = output.toFile().getName() + ".q";
            Path quantizedModel = parent == null ? Paths.get(name) : parent.resolve(name);
            Log.info("Saving quantized classification model to %s", quantizedModel);
            FastText quantized = fastText.quantize(output, fastText.getArgs());
            quantized.saveModel(quantizedModel);
        }

        try {
            String line;
            // FastTextClassifier classifier2 = FastTextClassifier.load(output);
            classifier = FastTextClassifier.load(path);
            int count = 0;
            int correct = 0;
            BufferedReader reader = new BufferedReader(new FileReader(testFile.toAbsolutePath().toString()));
            while ((line = reader.readLine()) != null){
                System.out.println(line);
                count++;
                String emotion = line.split(" ")[1];
                String pid = line.split(" ")[0];

                line = convert(line, unnecessaryWords.toAbsolutePath().toString());
                line = line.substring(line.indexOf(' ')+1);
                line = line.substring(line.indexOf(' ')+1);
                // results, only top three.
                List<ScoredItem<String>> res = classifier.predict(line, 1);
                System.out.println(res.size());

                for (ScoredItem<String> re : res) {
                    String out = re.toString().substring(9);
                    out = out.split(" ")[0];
                    if(emotion.equals(out)) {
                        correct++;
                    }
                    else{

                        System.out.println("p_id: " + pid + " Original: " + emotion + " Guessed: " + out);
                    }
                    /* if(emotion.equals("huzun") || emotion.equals("uzuntu") || emotion.equals("melankoli")){
                        if(out.equals("huzun") || out.equals("uzuntu") || out.equals("melankoli")) {
                            System.out.println("Kolektif.");
                            correct++;
                        }
                    }
                    else if(emotion.equals("diger")){
                        count--;
                    } */
                    /* if(emotion.equals("mutluluk") || emotion.equals("sasirma") || emotion.equals("diger") || emotion.equals("tiksinme") || emotion.equals("korku") || emotion.equals("ofke")){
                        if(out.equals("mutluluk") || out.equals("sasirma") || out.equals("diger") || out.equals("tiksinme") || out.equals("korku") || out.equals("ofke")) {
                            System.out.println("Kolektif.v2");
                            correct++;
                        }
                    }
                    */
                    /* else if(emotion.equals(out)){
                            correct++;
                    } */
                    // System.out.println(out);
                }
            }

            double accuracy = correct * 1.0 / count;

            System.out.println("Correct: " + correct);
            System.out.println("Count: "+ count);
            System.out.println("Accuracy: " + accuracy);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    public static String convert(String inputPar, String unnecessaryWordsPath){

        TurkishMorphology morphology = TurkishMorphology.createWithDefaults();

        String fileName = unnecessaryWordsPath;

        // This will reference one line at a time
        ArrayList<String> kelimeler = new ArrayList<String>();

        String line = null;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader =
                    new FileReader(fileName);

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
                            fileName + "'");
        } catch (IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fileName + "'");
            // Or we could just do this:
            // ex.printStackTrace();
        }

        String output = "";

        String[] parWords = inputPar.replaceAll("\\p{Punct}", " ").split("\\s+");

        for (int j = 0; j < parWords.length; j++) {
            for (int k = 0; k < kelimeler.size(); k++) {
                if (parWords[j].equals(kelimeler.get(k))) {
                    parWords[j] = "gereksiz";
                }
            }
        }

        for (int i = 0; i < parWords.length; i++) {
            if (!parWords[i].equals("gereksiz")) {
                parWords[i] = modified(parWords[i]);
                if(!parWords[i].equals("")){
                    // System.out.println("Kelime numarası " + i + ": " + parWords[i]);
                }
            }
        }

        for (int j = 0; j < parWords.length; j++) {
            for (int k = 0; k < kelimeler.size(); k++) {
                if (parWords[j].equals(kelimeler.get(k))) {
                    parWords[j] = "gereksiz";
                }
            }
        }

        // Log.info("Results: ");

        for (int i = 0; i < parWords.length; i++) {
            WordAnalysis results = morphology.analyze("");
            if (!parWords[i].equals(".")) {
                if (!parWords[i].equals("gereksiz")) {
                    if (!isNumeric(parWords[i])) {
                        results = morphology.analyze(parWords[i]);
                    }
                }
            }
            int t = 1;
            int z = results.analysisCount();
            for (SingleAnalysis result : results) {
                if (t == z) {
                    // Log.info(result.formatLong());
                    // Log.info("\tStems = " + result.getStems());
                    // Log.info("\tLemmas = " + result.getLemmas().get(result.getLemmas().size() - 1));
                    output = output + " " + result.getLemmas().get(result.getLemmas().size() - 1);
                }
                t++;
            }
        }
        return output;
    }

    public static String modified(final String input){
        final StringBuilder builder = new StringBuilder();
        for(final char c : input.toCharArray())
            if(Character.isLetterOrDigit(c))
                builder.append(Character.isLowerCase(c) ? c : Character.toLowerCase(c));
        return builder.toString();
    }

    public static void main(String[] args) {
        new HuzunClassifier().execute(args);
    }
}
