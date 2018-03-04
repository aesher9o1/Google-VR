package info.manipal.aesher.manipal.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.vr.sdk.widgets.pano.VrPanoramaView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;

import ai.api.AIConfiguration;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.ui.AIButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.manipal.aesher.manipal.R;
import info.manipal.aesher.manipal.adapters.Info;

/**
 * Created by aesher on 2/28/2018.
 */

public class BySpeech extends Fragment implements TextToSpeech.OnInitListener, AIButton.AIButtonListener {


    private TextToSpeech tts;
    private AIButton aiButton;
    private ImageLoaderTask backgroundImageLoaderTask;
    private VrPanoramaView panoWidgetView;




    @BindView(R.id.spot)
    TextView scan;


    @OnClick(R.id.spot)
        public void scanningBegin(){
        IntentIntegrator.forSupportFragment(BySpeech.this).initiateScan();
    }

    @BindView(R.id.info)
        TextView TInformation;

    @BindView(R.id.holder)
        CardView THolder;

    @BindView(R.id.progress)
        ProgressBar percentage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.spot,container,false);
        ButterKnife.bind(this,v);

        tts = new TextToSpeech(getContext(), this);
        aiButton = v.findViewById(R.id.micButton);
        panoWidgetView = v.findViewById(R.id.pano_view);





        isSoundPermission();





        final ai.api.android.AIConfiguration config = new ai.api.android.AIConfiguration("0a5fbe18d8104693b033a9540829f634",
                AIConfiguration.SupportedLanguages.English,
                ai.api.android.AIConfiguration.RecognitionEngine.System);


        config.setRecognizerStartSound(getResources().openRawResourceFd(R.raw.test_start));
        config.setRecognizerStopSound(getResources().openRawResourceFd(R.raw.test_stop));
        config.setRecognizerCancelSound(getResources().openRawResourceFd(R.raw.test_cancel));

        aiButton.initialize(config);
        aiButton.setResultsListener(this);


        return v;
    }

    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS){
            Locale loc = new Locale ("en", "IN");
            tts.setLanguage(loc);
            tts.setPitch(1.0f);
            tts.setSpeechRate(1.0f);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        aiButton.pause();
        panoWidgetView.pauseRendering();
    }

    @Override
    public void onResume() {
        super.onResume();
        aiButton.resume();
        panoWidgetView.resumeRendering();
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        panoWidgetView.shutdown();
        super.onDestroy();
    }

    private void speakOut(String text) {

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null,null);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);


        if(result!=null){
            Info information = new Info();
            speakOut(information.ShortInfo(Integer.valueOf(result.getContents())));
            THolder.setVisibility(View.VISIBLE);
            TInformation.setText(""+information.LongInfo(Integer.valueOf(result.getContents())));
        }
    }

    @Override
    public void onResult(AIResponse result) {
        ai.api.model.Result RESULT = result.getResult();
        String textS= RESULT.getFulfillment().getSpeech();

        speakOut(textS);
    }

    @Override
    public void onError(AIError error) {

    }

    @Override
    public void onCancelled() {

    }


    public void isSoundPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, 1);
            }
        }

    }


    private synchronized void loadPanoImage() {
        ImageLoaderTask task = backgroundImageLoaderTask;
        if (task != null && !task.isCancelled()) {
            // Cancel any task from a previous loading.
            task.cancel(true);
        }

        // pass in the name of the image to load from assets.
        VrPanoramaView.Options viewOptions = new VrPanoramaView.Options();
        viewOptions.inputType = VrPanoramaView.Options.TYPE_MONO;

        // use the name of the image in the assets/ directory.
        URL panoImageName = null;
        try {
            panoImageName = new URL("http://www.iaestemuj.in/iaeste/login/images/radar/manipal.jpg");
            task = new ImageLoaderTask(panoWidgetView, viewOptions, panoImageName);
            task.execute(getActivity().getAssets());
            backgroundImageLoaderTask = task;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }



    }





    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadPanoImage();
        scan.setVisibility(View.GONE);
        percentage.setVisibility(View.VISIBLE);

    }



    class ImageLoaderTask  extends AsyncTask<AssetManager, Void, Bitmap> {

        private static final String TAG = "ImageLoaderTask";
        private URL  assetName;
        private final WeakReference<VrPanoramaView> viewReference;
        private final VrPanoramaView.Options viewOptions;
        private  WeakReference<Bitmap> lastBitmap = new WeakReference<>(null);
        private String lastName;

        public ImageLoaderTask(VrPanoramaView view, VrPanoramaView.Options viewOptions, URL assetName) {
            viewReference = new WeakReference<>(view);
            this.viewOptions = viewOptions;
            this.assetName = assetName;
        }

        @Override
        protected Bitmap doInBackground(AssetManager... assetManagers) {

            int size;

            URLConnection connection;

            try {
                connection = assetName.openConnection();
                size = connection.getContentLength();
                Log.w("TotalSize",""+size);

                InputStream istr = assetName.openStream();
                istr = connection.getInputStream();

                Bitmap b = BitmapFactory.decodeStream(istr);
                lastBitmap = new WeakReference<>(b);
                return b;

            }
            catch (Exception e){
                Log.e(TAG, "Could not decode default bitmap: " + e);
                return null;
            }



        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            panoWidgetView.setVisibility(View.VISIBLE);
            final VrPanoramaView vw = viewReference.get();
            if (vw != null && bitmap != null) {
                vw.loadImageFromBitmap(bitmap, viewOptions);
                scan.setVisibility(View.VISIBLE);
                percentage.setVisibility(View.INVISIBLE);
            }
        }





    }






}
