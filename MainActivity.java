package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.tensorflow.lite.Interpreter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    TextView resultTv;
    Button predict;
    Interpreter interpreter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            interpreter= new Interpreter(loadModelFile(),null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        editText= findViewById(R.id.editText);
     resultTv= findViewById(R.id.textView);
    predict= findViewById(R.id.button);
    predict.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            float f=doInference(editText.getText().toString());
            resultTv.setText("result: "+f);
        }
    });

}

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor=this.getAssets().openFd("model.tflite");
        FileInputStream inputStream=new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel=inputStream.getChannel();
        long startOffset=fileDescriptor.getStartOffset();
        long declareLength=fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffset,declareLength);
    }
    public float doInference(String inputString) {
        float[] inputVal=new float[1];
        inputVal[0]=Float.parseFloat(inputString);
        float[][] output=new float[1][1];
        interpreter.run(inputVal,output);
        float inferredValue=output[0][0];
        return  inferredValue;
    }
}