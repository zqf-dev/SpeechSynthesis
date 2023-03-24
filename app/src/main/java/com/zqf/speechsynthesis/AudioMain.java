package com.zqf.speechsynthesis;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.hjq.toast.Toaster;
import com.huaweicloud.sdk.core.auth.BasicCredentials;
import com.huaweicloud.sdk.core.exception.ConnectionException;
import com.huaweicloud.sdk.core.exception.RequestTimeoutException;
import com.huaweicloud.sdk.core.exception.ServiceResponseException;
import com.huaweicloud.sdk.core.http.HttpConfig;
import com.huaweicloud.sdk.core.utils.StringUtils;
import com.huaweicloud.sdk.sis.v1.SisClient;
import com.huaweicloud.sdk.sis.v1.model.PostCustomTTSReq;
import com.huaweicloud.sdk.sis.v1.model.RunTtsRequest;
import com.huaweicloud.sdk.sis.v1.model.RunTtsResponse;
import com.huaweicloud.sdk.sis.v1.model.TtsConfig;
import com.huaweicloud.sdk.sis.v1.region.SisRegion;

import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class AudioMain extends AppCompatActivity {

    private EditText text;
    private Button startSoundRecording;
    private Button startPlay;
    private Handler handler;
    // 保存合成的base64字符串
    private String base64Data;
    // 合成音频路径
    private String createFilePath;
    // 客户端请求
    private SisClient client;
    private MediaPlayerService mediaPlayerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stts);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initView();
        initResoureces();
    }

    // 初始化界面
    private void initView() {
        text = findViewById(R.id.input_text);
        text.setText(Config.inputString);
        startSoundRecording = findViewById(R.id.start);
        startPlay = findViewById(R.id.startplay);
        startSoundRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        String sttsRequestText = getSttsResponse();
                        Message message = new Message();
                        Bundle mBundle = new Bundle();
                        mBundle.putString("result", sttsRequestText);
                        message.setData(mBundle);
                        handler.sendMessage(message);
                    }
                };
                new Thread(runnable).start();
            }
        });
        startPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!StringUtils.isEmpty(base64Data)) {
                    createFilePath = mediaPlayerService.createAudioFile(getBaseContext(), "wav", base64Data);
                    mediaPlayerService.startPlay(createFilePath);
                }
            }
        });
    }

    /**
     * 初始化资源
     *
     * @return
     */
    private void initResoureces() {
        BasicCredentials auth = new BasicCredentials()
                .withAk(Config.AK)
                .withSk(Config.SK)
                .withProjectId(Config.PROJECT_ID);
        HttpConfig config = HttpConfig.getDefaultHttpConfig();
        config.withIgnoreSSLVerification(true);
        client = SisClient.newBuilder()
                .withHttpConfig(config)
                .withCredential(auth)
                .withRegion(SisRegion.valueOf(Config.REGION))
                .build();
        handler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message message) {
                super.handleMessage(message);
                if (message.what == 0) {
                    Bundle bundle = message.getData();
                    String rstr = bundle.getString("result");
                    Toaster.show(rstr);
                } else {
                    Log.e("Unexpected value: ", String.valueOf(message.what));
                }
            }
        };
        mediaPlayerService = new MediaPlayerService();
    }


    // 设置请求体

    private RunTtsRequest getRunTtsRequest() {
        TtsConfig configbody = new TtsConfig();
        configbody.setSpeed(0);
        configbody.setAudioFormat(TtsConfig.AudioFormatEnum.fromValue("wav"));
        configbody.setSampleRate(TtsConfig.SampleRateEnum.fromValue("8000"));
        configbody.setProperty(TtsConfig.PropertyEnum.fromValue("chinese_huaxiaomei_common"));
        RunTtsRequest request = new RunTtsRequest();
        PostCustomTTSReq body = new PostCustomTTSReq();
        body.withConfig(configbody);
        if (!StringUtils.isEmpty(text.getText().toString())) {
            body.withText(text.getText().toString());
        } else {
            body.withText("请输入合成文本");
        }
        request.withBody(body);
        return request;
    }

    // 发送请求
    private String getSttsResponse() {
        RunTtsRequest request = getRunTtsRequest();
        String ttsString = "";
        try {
            RunTtsResponse response = client.runTts(request);
            if (response.getResult().getData() != null) {
                base64Data = response.getResult().getData();
                ttsString = "合成成功";
            } else {
                ttsString = "合成失败";
            }
            Log.i("info", ttsString);
        } catch (Exception e) {
            Log.e("error", e.toString());
        }
        return ttsString;
    }

    @Override
    protected void onPause() {
        mediaPlayerService.stopMyPlayer(createFilePath);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mediaPlayerService.stopMyPlayer(createFilePath);
        super.onDestroy();
    }
}
