package com.eliftech.hawk.voicerecognition;

import java.util.ArrayList;
import java.util.List;
import java.lang.String;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import com.eliftech.hawk.voicerecognition.api.APIService;
import com.eliftech.hawk.voicerecognition.api.Command;
import com.eliftech.hawk.voicerecognition.api.ServerResponse;


public class VoiceRecognition extends Activity {

    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;

    private ListView mlvTextMatches;
    private Spinner msTextMatches;
    private Button mbtSpeak;
    private EditText endpoint;

    private String baseUrl = "https://bid58j2782.execute-api.us-west-2.amazonaws.com/prod/t2c";//for send text command

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_recognition);
        mlvTextMatches = (ListView) findViewById(R.id.lvTextMatches);
        msTextMatches = (Spinner) findViewById(R.id.sNoOfMatches);
        mbtSpeak = (Button) findViewById(R.id.btSpeak);
        endpoint = (EditText) findViewById((R.id.endpoint));
        checkVoiceRecognition();
    }

    public void checkVoiceRecognition() {
        // Check if voice recognition is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0) {
            mbtSpeak.setEnabled(false);
            mbtSpeak.setText("Voice recognizer not present");
            Toast.makeText(this, "Voice recognizer not present",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void speak(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        // Specify the calling package to identify your application
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
                .getPackage().getName());

        // Given an hint to the recognizer about what the user is going to say
        //There are two form of language model available
        //1.LANGUAGE_MODEL_WEB_SEARCH : For short phrases
        //2.LANGUAGE_MODEL_FREE_FORM  : If not sure about the words or phrases and its domain.
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        // Set default speech recognition language to english
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");

        //hint when you can start speaking
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Tell your command now");

        //set maximum length of silence after we stop our speech
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, "1500");

        // If number of Matches is not selected then return show toast message
        if (msTextMatches.getSelectedItemPosition() == AdapterView.INVALID_POSITION) {
            Toast.makeText(this, "Please select No. of Matches from spinner",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int noOfMatches = Integer.parseInt(msTextMatches.getSelectedItem()
                .toString());
        // Specify how many results you want to receive. The results will be
        // sorted where the first result is the one with higher confidence.
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, noOfMatches);
        //Start the Voice recognizer activity for the result.
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE)

            //If Voice recognition is successful then it returns RESULT_OK
            if(resultCode == RESULT_OK) {

                final ArrayList<String> textMatchList = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                if (!textMatchList.isEmpty()) {
                    sendCommand(textMatchList.get(0));

                // populate the Matches
                mlvTextMatches
                        .setAdapter(new ArrayAdapter<String>(this,
                                android.R.layout.simple_list_item_1,
                                textMatchList));

                mlvTextMatches
                        .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                sendCommand(textMatchList.get(position));
                            }
                        });


                }
                //Result code for various error.
            }else if(resultCode == RecognizerIntent.RESULT_AUDIO_ERROR){
                showToastMessage("Audio Error");
            }else if(resultCode == RecognizerIntent.RESULT_CLIENT_ERROR){
                showToastMessage("Client Error");
            }else if(resultCode == RecognizerIntent.RESULT_NETWORK_ERROR){
                showToastMessage("Network Error");
            }else if(resultCode == RecognizerIntent.RESULT_NO_MATCH){
                showToastMessage("No Match");
            }else if(resultCode == RecognizerIntent.RESULT_SERVER_ERROR){
                showToastMessage("Server Error");
            }
        super.onActivityResult(requestCode, resultCode, data);
    }
    /**
     * Helper method to show the toast message
     **/
    void showToastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    //------API services

    APIService getAPIService(String baseUrl){
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(baseUrl).build();
        APIService apiService = restAdapter.create(APIService.class);
        return apiService;
    }

    String getEndpoint(){
        String endpointSpec = endpoint.getText().toString();
        if(URLUtil.isValidUrl(endpointSpec)){
            baseUrl = endpointSpec;
        }
        return baseUrl;
    }

    public void sendCommand(final String command){
        showToastMessage("Sending command: " + command);

        baseUrl = getEndpoint();

        APIService apiService = getAPIService(baseUrl);
        Command sendCommand = new Command(command);
        apiService.sendCommand(sendCommand, new retrofit.Callback<ServerResponse>(){
            @Override
            public void success(ServerResponse result, Response response) {
                String message = "response from server: " + result.getStatus() + " command: " + result.getCommand();
                showToastMessage(message);
                Log.d("DEBUG",message);
            }

            @Override
            public void failure(RetrofitError error) {
                showToastMessage("Error: "+error);
            }
        });
    }
}
