package com.cgs.vision.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.cgs.vision.Datos;
import com.cgs.vision.R;
import com.cgs.vision.Rectangulo;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class  MainActivity extends AppCompatActivity {

    SurfaceView mCameraView;
    TextView mTextView;
    CameraSource mCameraSource;
    FloatingActionButton mBtn;
    Spinner spinner;
    ImageView flecha;
    View view;
    ConstraintLayout layout;
    Rectangulo rectangulo;
    String sangrado;
    TextRecognizer textRecognizer;


    private static final String TAG = "MainActivity";
    private static final int requestPermissionID = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flecha = findViewById(R.id.flecha2);
        spinner = findViewById(R.id.tipo);
        mCameraView = findViewById(R.id.surfaceView);
        mCameraView.setWillNotDraw(false);
        mTextView = findViewById(R.id.text_view);
        mBtn = findViewById(R.id.btn_text);

        layout = findViewById(R.id.layout);

        rectangulo = new Rectangulo(layout,getApplicationContext());
        sangrado = new Datos(getApplicationContext()).getAjustes("Sangrado");

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.tipos, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        //  onmBtnClicked();


        flecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Intent i = new Intent(MainActivity.this, Local.class);
               // startActivity(i);
                finish();
            }
        });

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onmBtnClicked();
            }
        });

        startCameraSource();
    }

    public void onmBtnClicked() {
        Intent i = new Intent(this, Result.class);
        i.putExtra("ocrText", mTextView.getText().toString());
        i.putExtra("tipo", spinner.getSelectedItem().toString());
        finish();
        startActivity(i);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != requestPermissionID) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mCameraSource.start(mCameraView.getHolder());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //si no tiene el permiso deberia volver hacia atras
        else
            finish();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Comprobamos orientacion
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Display display = getWindowManager().getDefaultDisplay();
            final Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            //inicializamos camara y su resolucion

            mCameraSource.stop();
            mCameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    // .setRequestedPreviewSize(1280, 1024)
                    .setRequestedPreviewSize(width, height-1000)
                    .setAutoFocusEnabled(true)
                    .setRequestedFps(2.0f)
                    .build();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }
    private void startCameraSource() {

        //Create the TextRecognizer
        final TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if (!textRecognizer.isOperational()) {
            Log.w(TAG, "Detector dependencies not loaded yet");
        } else {
            Display display = getWindowManager().getDefaultDisplay();
            final Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;

            int test = this.getResources().getConfiguration().orientation;
            if(test != Configuration.ORIENTATION_PORTRAIT)
            {
                width = size.y;
                height = size.x;
            }
            mCameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    // .setRequestedPreviewSize(1280, 1024)
                    //.setRequestedPreviewSize(2220, width)
                    .setRequestedPreviewSize(height, width)
                    .setAutoFocusEnabled(true)
                    .setRequestedFps(30.0f)
                    .build();

            mCameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                        {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    requestPermissionID);
                            return;
                        }
                        mCameraSource.start(mCameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    mCameraSource.stop();
                }
            });

            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {
                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> items = detections.getDetectedItems();

                    if (items.size() != 0) {
                        mTextView.post(new Runnable() {
                            @Override
                            public void run()
                            {
                                StringBuilder stringBuilder = new StringBuilder();
                                ArrayList<Float> valoresX = new ArrayList<>();
                                int llaves=0;
                                for (int i = 0; i < items.size(); i++)
                                {
                                    TextBlock item = items.valueAt(i);
                                    float xBloque = xMasCerca(item.getCornerPoints());
                                    String bloque = item.getValue();
                                    //Borramos los cuadrados en pantalla del escaneo anterior
                                    if(i==0)
                                        rectangulo.clearRectangulo();

                                    int cont = 0;
                                    for(Text line : item.getComponents())
                                    {
                                        String linea = line.getValue();
                                        //SANGRADO INTELIGENTE
                                        if(sangrado.equals("SI") || sangrado.equals(""))
                                        {
                                            float valorX = xMasCerca(line.getCornerPoints());
                                            //Si es la primera iteracion
                                            if (i == 0)
                                            {
                                                if (valoresX.isEmpty() || ((valoresX.get(valoresX.size() - 1) < valorX) && (valorX - valoresX.get(valoresX.size() - 1) > 15)))
                                                    valoresX.add(valorX);
                                            }
                                            else
                                            {
                                                boolean fin = false;
                                                //contador para recorrer la lista desde el final
                                                cont = valoresX.size() - 1;
                                                while (!fin)
                                                {
                                                    //si valoresX esta vacío
                                                    if (cont == -1) {
                                                        fin = true;
                                                    }

                                                    else if (valorX < valoresX.get(cont)) {
                                                        cont--;
                                                    }
                                                    else
                                                    {
                                                        //Recorremos los valores guardados del eje X
                                                        //
                                                        if ((cont == valoresX.size() - 1))
                                                        {
                                                            //Si el ultimo valor es menor por más de 15 unidades
                                                            //añadimos al final
                                                            if (valorX - valoresX.get(valoresX.size() - 1) > 15)
                                                                valoresX.add(valorX);

                                                            stringBuilder.append(sangrado(cont + 1));
                                                            linea = addSangrado(linea, cont + 1);
                                                            fin = true;
                                                        }
                                                        //si el ultimo valor es igual al valor recibido
                                                        else if (xMasCerca(item.getCornerPoints()) == valoresX.get(cont))
                                                        {
                                                            stringBuilder.append(sangrado(cont));
                                                            linea = addSangrado(linea, cont);
                                                            fin = true;
                                                        }
                                                        else {
                                                            float aux = getCercano(valoresX.get(cont + 1), valoresX.get(cont), xMasCerca(item.getCornerPoints()));
                                                            if (aux == 0)
                                                            {
                                                                stringBuilder.append(sangrado(cont));
                                                                linea = addSangrado(linea, cont);
                                                                fin = true;
                                                            }
                                                            else
                                                            {
                                                                stringBuilder.append(sangrado(cont + 1));
                                                                linea = addSangrado(linea, cont + 1);
                                                                fin = true;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        stringBuilder.append(linea);

                                        if(findWords(bloque,spinner.getSelectedItem().toString()))
                                            stringBuilder.append("\n").append(sangrado(cont+1)).append("{");

                                        stringBuilder.append("\n");
                                        rectangulo.drawRectangulo(line.getCornerPoints());
                                    }
                                    //se hace un estudio de la linea para generar llaves donde se crea necsario.
                                  /*  if(findWords(bloque,spinner.getSelectedItem().toString())) {
                                        stringBuilder.append("{");
                                        //llaves++;
                                    }*/
                                  /*  else if(llaves>0)
                                    {
                                        stringBuilder.append("\n}");
                                        llaves--;
                                    }*/
                                    //stringBuilder.append("\n");

                                }
                                mTextView.setText(stringBuilder.toString());
                            }
                        });
                    }
                }
            });
        }
    }

    private float xMasCerca(Point[] p)
    {
        return Math.min(p[0].x,p[3].x);
    }
    private String sangrado(int sangrado)
    {
        String resultado = null;
        if(sangrado==0)
            return "";
        for(int i=0;i<sangrado;i++)
        {
            if(resultado==null)
                resultado="   ";
            else
                resultado = resultado+"   ";
        }
        return resultado;
    }
    private float getCercano(float superior,float inferior, float valor)
    {
        if(superior-valor < valor-inferior)
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }
    private String addSangrado(String text,int sangrado)
    {
        String result = null;
        int lastChange=0;
        for(int i=0;i<text.length();i++)
        {
            if('\n' == text.charAt(i))
            {
                if(result==null)
                    result = text.substring(lastChange,i+1) + sangrado(sangrado);
                else
                    result += text.substring(lastChange,i+1) + sangrado(sangrado);;
                lastChange = i+1;
            }
            if(i==text.length()-1 && i!=lastChange)
            {
                if(result==null)
                    result = text.substring(lastChange,i+1);
                else
                    result += text.substring(lastChange,i+1);
            }
        }
        return result;
    }
    //Busca patrones para encontrar llaves que hayna podido no entrar
    private boolean findWords(String bloque,String tipo)
    {
        String[] patterns;
        switch (tipo)
        {
            case "JAVA":
                patterns = new String[]{".*(\\s|\\n)*public\\s*[A-Za-z0-9<>]+\\s*([A-Za-z0-9]+\\s*)+[(][\\s\\S]+[)]\\s*(throws|implements\\s[A-Za-z0-9]+)?"
                        , ".*(\\s|\\n)*private\\s*[A-Za-z0-9<>]+\\s*[A-Za-z0-9]+\\s*[(][\\s\\S]++[)]\\s*(throws|implements\\s*[A-Za-z]+)?"
                        , ".*(\\s|\\n)*if\\s*[(][\\s\\S]+[)]"
                        , ".*(\\s|\\n)*else\\s*(\\n|$)"
                        , ".*(\\s|\\n)*else\\s+if\\s*[(][\\s\\S]+[)]"
                        , ".*(\\s|\\n)*for\\s*[(][\\s\\S]+[)]"
                        , ".*(\\s|\\n)*try\\s*(\\n|$)"
                        , ".*(\\s|\\n)*catch\\s*[\\s\\S]+\\n*"
                        , ".*(\\s|\\n)*finally\\s*(\\n|$)"
                        , ".*(\\s|\\n)*switch\\s*[(][\\s\\S]+[)]"
                        , ".*(\\s|\\n)*while\\s*[(][\\s\\S]+[)]"
                        , ".*(\\s|\\n)do\\s*(\\n|$)"};
                break;
            case "C":
                patterns = new String[]{".*(\\s|\\n)*[a-zA-z0-9]+\\s+main.*"
                        ,".*(\\s|\\n)*(unsigned|signed)*\\s+(int|char|float|double|short|long)\\s+[a-zA-Z0-9]+[(][\\s\\S]+[)]\\n"
                        , ".*(\\s|\\n)*if\\s*[(][\\s\\S]+[)]"
                        , ".*(\\s|\\n)*else\\s*(\\n|$)"
                        , ".*(\\s|\\n)*else\\s+if\\s*[(][\\s\\S]+[)]"
                        , ".*(\\s|\\n)*switch\\s*[(][\\s\\S]+[)]"
                        , ".*(\\s|\\n)*while\\s*[(][\\s\\S]+[)]"
                        , ".*(\\s|\\n)do\\s*(\\n|$)"
                        , ".*(\\s|\\n)*for\\s*[(][\\s\\S]+[)]"
                        , ".*(\\s|\\n)*try\\s*(\\n|$)"
                        ,".*(\\s|\\n)*enum\\s+[a-zA-Z0-9]"};
                break;
            case "C++":
                patterns = new String[]{".*(\\s|\\n)*[a-zA-z0-9]+\\s+main.*"
                        ,".*(\\s|\\n)*(short|unsigned|long|signed)*\\s+(int|char|float|double)\\s+[a-zA-Z0-9]+[(][\\s\\S]+[)]\\n"
                        , ".*(\\s|\\n)*if\\s*[(][\\s\\S]+[)]"
                        , ".*(\\s|\\n)*else\\s*(\\n|$)"
                        , ".*(\\s|\\n)*else\\s+if\\s*[(][\\s\\S]+[)]"
                        , ".*(\\s|\\n)*switch\\s*[(][\\s\\S]+[)]"
                        , ".*(\\s|\\n)*while\\s*[(][\\s\\S]+[)]"
                        , ".*(\\s|\\n)do\\s*(\\n|$)"
                        , ".*(\\s|\\n)*for\\s*[(][\\s\\S]+[)]"
                        , ".*(\\s|\\n)*try\\s*(\\n|$)"
                        , ".*(\\s|\\n)*catch\\s*[\\s\\S]+\\n"
                        ,".*(\\s|\\n)*enum\\s+[a-zA-Z0-9]"};
                break;
            case "JAVASCRIPT":
                patterns = new String[]{".*(\\n|\\s)*function\\s+[a-zA-z0-9]+\\s*[(][\\s\\S]+[)]"
                        , ".*(\\s|\\n)*if\\s*[(][\\s\\S]+[)]"
                        , ".*(\\s|\\n)*else\\s*(\\n|$)"
                        , ".*(\\s|\\n)*else\\s+if\\s*[(][\\s\\S]+[)]"
                        , ".*(\\s|\\n)*switch\\s*[(][\\s\\S]+[)]"
                        , ".*(\\s|\\n)*while\\s*[(][\\s\\S]+[)]"
                        , ".*(\\s|\\n)do\\s*(\\n|$)"
                        , ".*(\\s|\\n)*for\\s*[(][\\s\\S]+[)]"
                        , ".*(\\s|\\n)*try\\s*(\\n|$)"
                        , ".*(\\s|\\n)*catch\\s*[\\s\\S]+\\n"
                        , ".*(\\s|\\n)*finally\\s*(\\n|$)"};
                break;
            default:
                patterns = new String[]{""};
                break;
        }

        for (String s : patterns) {
            Pattern pattern = Pattern.compile(s);
            Matcher m = pattern.matcher(bloque);
            if (m.find()) {
                return true;
            }
        }
        return false;
    }
}
