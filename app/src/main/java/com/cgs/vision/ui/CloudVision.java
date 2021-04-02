package com.cgs.vision.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cgs.vision.Datos;
import com.cgs.vision.R;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.Block;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.Page;
import com.google.api.services.vision.v1.model.Paragraph;
import com.google.api.services.vision.v1.model.Symbol;
import com.google.api.services.vision.v1.model.Word;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CloudVision extends AppCompatActivity {
    private static final String CLOUD_VISION_API_KEY = "AIzaSyCk_iHXn5ZF8KE8cmw9ACjYcyKvUoWLno8";
    public static final String FILE_NAME = "temp.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private static final int MAX_LABEL_RESULTS = 10;
    private static final int MAX_DIMENSION = 1200;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;

    private static final int requestPermissionID = 101;

    private TextView mImageDetails;
    private ImageView mMainImage,flecha;
    private String tipo;
    private BatchAnnotateImagesResponse response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_vision);
        mImageDetails = findViewById(R.id.image_details);
        mMainImage = findViewById(R.id.main_image);
        flecha = findViewById(R.id.flecha2);
        Datos datos = new Datos(getApplicationContext());

        flecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CloudVision.this, MenuMain.class);
                finish();
                startActivity(i);
            }
        });

        //Comprobamos que tengamos permiso para acceder a la camara
        if (datos.checkPer(Manifest.permission.CAMERA, getApplicationContext())
            ||datos.checkPer(Manifest.permission.READ_EXTERNAL_STORAGE, getApplicationContext()))
        {
            startCamera();
        }
        else
        {
            //pedimos al usuario los permisos necesarios
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 2);
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != requestPermissionID) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults[0] != PackageManager.PERMISSION_GRANTED)
            finish();
    }

    public void startCamera() {
        /*if (PermissionUtils.requestPermission(
                this,
                CAMERA_PERMISSIONS_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)) {*/
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext()
                                .getPackageName()+ ".provider" , getCameraFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        /*}*/
    }
    public File getCameraFile() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            uploadImage(data.getData());
        }
        else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext()
                            .getPackageName() + ".provider", getCameraFile());
            uploadImage(photoUri);
        }
    }
    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
            /*    Bitmap bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                2000);*/
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                bitmap = rotateBitmap(bitmap);
                callCloudVision(bitmap);
                mMainImage.setImageBitmap(bitmap);

            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                Toast.makeText(this, "ERROR", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Log.d(TAG, "Image picker gave us a null image.");
            Toast.makeText(this, "ERROR", Toast.LENGTH_LONG).show();
        }
    }
    private Bitmap rotateBitmap(Bitmap bitmap) throws IOException {
        int rotate = 0;
        ExifInterface exif;
        exif = new ExifInterface(getCameraFile().getAbsolutePath());
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private void callCloudVision(final Bitmap bitmap) {
        mImageDetails.setText("cargando...");

        try {
            AsyncTask<Object, Void, String> labelDetectionTask = new LableDetectionTask(this, prepareAnnotationRequest(bitmap));
            labelDetectionTask.execute();
        } catch (IOException e) {
            Log.d(TAG, "failed to make API request because of other IOException " +
                    e.getMessage());
        }
    }
    private class LableDetectionTask extends AsyncTask<Object, Void, String> {
        private final WeakReference<CloudVision> mActivityWeakReference;
        private Vision.Images.Annotate mRequest;

        LableDetectionTask(CloudVision activity, Vision.Images.Annotate annotate) {
            mActivityWeakReference = new WeakReference<>(activity);
            mRequest = annotate;
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                Log.d(TAG, "created Cloud Vision request object, sending request");
                BatchAnnotateImagesResponse response = mRequest.execute();
                return convertResponseToString(response);

            } catch (GoogleJsonResponseException e) {
                Log.d(TAG, "Error en API " + e.getContent());
            } catch (IOException e) {
                Log.d(TAG, "Error en la petición de API " +
                        e.getMessage());
            }
            return "Error en la peticion de la API";
        }

        protected void onPostExecute(final String result) {
            CloudVision activity = mActivityWeakReference.get();
            if (activity != null && !activity.isFinishing()) {
                TextView imageDetail = activity.findViewById(R.id.image_details);
                imageDetail.setText(result);
                AlertDialog.Builder builder = new AlertDialog.Builder(CloudVision.this);
                final String[] list = {"JAVA","PYTHON","C","C++","JAVASCRIPT","OTROS"};
                final StringBuilder aux = new StringBuilder();
                builder.setSingleChoiceItems(list, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tipo = list[which];
                        String[] segments = result.split("(\\n)");
                        Datos datos = new Datos(getApplicationContext());
                        String sangradoGuardado = datos.getAjustes("Sangrado");
                        int sangrado = 0;
                        aux.delete(0,aux.length());
                        if(sangradoGuardado.equals("SI") || sangradoGuardado.equals(""))
                        {
                            switch (tipo)
                            {
                                case "JAVA":
                                case "C":
                                case "C++":
                                case "JAVASCRIPT":
                                    for (String s : segments) {
                                        for (int i = 0; i < sangrado; i++) {
                                            aux.append("   ");
                                        }
                                        if (s.contains("{"))
                                            sangrado++;
                                        if (s.contains("}"))
                                            sangrado--;

                                        aux.append(s).append("\n");
                                    }
                                    break;
                                case "PYTHON":
                                    List<Page> pages = response.getResponses().get(0).getFullTextAnnotation().getPages();
                                    List<Integer> coordenadas = new ArrayList<>();
                                    for (Page p : pages)
                                    {
                                        List<Block> blocks = p.getBlocks();
                                        for (Block bloque : blocks)
                                        {
                                            String line = getBlockText(bloque);
                                            int xBlock = Math.max(bloque.getBoundingBox().getVertices().get(0).getX(),
                                                         bloque.getBoundingBox().getVertices().get(3).getX());
                                            //Recorremos las coordenadas de cada linea que contiene el bloque analizado
                                            if(coordenadas.isEmpty())
                                                coordenadas.add(xBlock);
                                            else if(xBlock > coordenadas.get(0))
                                            {
                                                int size = coordenadas.size();
                                                for(int i=0;i<size-1;i++)
                                                {
                                                    if(xBlock < coordenadas.get(i) || xBlock == coordenadas.get(i) )
                                                    {
                                                        aux.append(sangrado(i));
                                                        break;
                                                    }
                                                    else if(i == size-1)
                                                    {
                                                        coordenadas.add(xBlock);
                                                        aux.append(sangrado(i+1));
                                                    }
                                                }
                                            }
                                            aux.append(line).append("\n");
                                        }
                                    }
                                    break;
                                default:
                                    aux.append(result);
                                    break;
                            }
                        }
                        else
                        {
                            aux.append(result);
                        }
                    }
                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(CloudVision.this,Result.class);
                        i.putExtra("ocrText",aux.toString());
                        i.putExtra("tipo",tipo);
                        finish();
                        startActivity(i);
                    }
                });
                builder.setCancelable(false);
                builder.show();
            }
        }
    }
    private String sangrado(int i)
    {
        String s = "";
        for(int j=0;j<i;j++)
        {
            s+="   ";
        }
        return s;
    }
    private String convertResponseToString(BatchAnnotateImagesResponse response) {
        StringBuilder message = new StringBuilder("I found these things:\n\n");
        this.response = response;

        String visionApiResult ="";
        AnnotateImageResponse imageResponses;
        List<EntityAnnotation> entityAnnotations;
        List<EntityAnnotation> labels;

        List<Page> pages = response.getResponses().get(0).getFullTextAnnotation().getPages();
        for(Page p: pages)
        {
            List<Block> blocks = p.getBlocks();
            for(Block b : blocks)
            {
                String line = getBlockText(b);
            }
        }

        String respuesta;
        if(!response.getResponses().get(0).isEmpty()) {
            respuesta = response.getResponses().get(0).getFullTextAnnotation().getText();

            imageResponses= response.getResponses().get(0);
            entityAnnotations = imageResponses.getTextAnnotations();
            labels= response.getResponses().get(0).getLabelAnnotations();


            if (labels != null) {
                for (EntityAnnotation label : labels) {
                    message.append(String.format(Locale.US, "%.3f: %s", label.getScore(), label.getDescription()));
                    message.append("\n");
                }
            } else {
                message.append("nada");
                message.delete(0,message.length());
                message.append(respuesta);
            }
        }
        else {
            respuesta = "No se encontró nada";
            message.append(respuesta);
        }

        return message.toString();

    }
    private Vision.Images.Annotate prepareAnnotationRequest(final Bitmap bitmap) throws IOException {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        VisionRequestInitializer requestInitializer =
                new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                    /**
                     * We override this so we can inject important identifying fields into the HTTP
                     * headers. This enables use of a restricted cloud platform API key.
                     */
                    @Override
                    protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                            throws IOException {
                        super.initializeVisionRequest(visionRequest);

                        String packageName = getPackageName();
                        visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                        String sig = com.google.sample.cloudvision.PackageManagerUtils.getSignature(getPackageManager(), packageName);

                        visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                    }
                };

        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
        builder.setVisionRequestInitializer(requestInitializer);

        Vision vision = builder.build();

        BatchAnnotateImagesRequest batchAnnotateImagesRequest = new BatchAnnotateImagesRequest();
        batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>()
        {{
            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

            // Add the image
            Image base64EncodedImage = new Image();
            // Convert the bitmap to a JPEG
            // Just in case it's a format that Android understands but Cloud Vision
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Base64 encode the JPEG
            base64EncodedImage.encodeContent(imageBytes);
            annotateImageRequest.setImage(base64EncodedImage);

            // add the features we want
            annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                Feature labelDetection = new Feature();
                labelDetection.setType("DOCUMENT_TEXT_DETECTION");
                labelDetection.setMaxResults(MAX_LABEL_RESULTS);
                add(labelDetection);
            }});

            // Add the list of one thing to the request
            add(annotateImageRequest);
        }});

        Vision.Images.Annotate annotateRequest =
                vision.images().annotate(batchAnnotateImagesRequest);
        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotateRequest.setDisableGZipContent(true);
        Log.d(TAG, "created Cloud Vision request object, sending request");

        return annotateRequest;
    }
    private static String getBlockText(Block block)
    {
        StringBuilder result = new StringBuilder();
        for(Paragraph pa : block.getParagraphs())
        {
            for(Word w : pa.getWords())
            {
                for(Symbol s :w.getSymbols())
                {
                    result.append(s.getText());
                }
                result.append(" ");
            }
        }
        return result.toString();
    }
}
