package ee.ut.cs.dsg.esperadapter.util;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PerformanceFileBuilder {

    private CSVWriter writer;
    private String platform;
    private int parallelism;

    public PerformanceFileBuilder(String fileName, String platform, int parallelism) {
        try {
            File file = new File(fileName);
            if(!file.exists()){
                file.createNewFile();
                this.writer = new CSVWriter(new FileWriter(file, true));
                String[] firstRow = new String[]{"Experiment-Name", "Throughput", "InputSize", "SecondsPassed", "ReadLine Time", "ParsingTime"};
                this.writer.writeNext(firstRow);
                this.writer.flush();
            }
            this.writer = new CSVWriter(new FileWriter(file, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.platform = platform;
        this.parallelism = parallelism;
    }

    public void register(double throughput, String expName, long inputSize, double secondsPassed){
        String[] row = new String[]{expName, String.valueOf(throughput), String.valueOf(inputSize+1), String.valueOf(secondsPassed)};
        writer.writeNext(row);
        try {
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void register(double throughput, String expName, long inputSize, double secondsPassed, double readTime){
        String[] row = new String[]{expName, String.valueOf(throughput), String.valueOf(inputSize+1), String.valueOf(secondsPassed), String.valueOf(readTime)};
        writer.writeNext(row);
        try {
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
