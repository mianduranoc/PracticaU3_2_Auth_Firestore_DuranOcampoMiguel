package mx.edu.ittepic.practicau3_2_firestore_duranocampomiguelangel;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText nombre,precio,cantidad;
    Button insertar,actualizar,eliminar;
    LinearLayout layo;
    int opcion;
    String clave;
    FirebaseFirestore baseDatos;
    Map<String,Object> datos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nombre=findViewById(R.id.nombre);
        precio=findViewById(R.id.precio);
        cantidad=findViewById(R.id.cantidad);
        insertar=findViewById(R.id.agregar);
        actualizar=new Button(MainActivity.this);
        eliminar=new Button(MainActivity.this);
        baseDatos=FirebaseFirestore.getInstance();
        datos=new HashMap<>();
        layo=findViewById(R.id.layo);
        opcion=Integer.parseInt(getIntent().getExtras().get("operacion").toString());
        clave=getIntent().getExtras().get("clave").toString();
        if (opcion==1){
            layo.removeView(insertar);
            layo.addView(actualizar,4);
            layo.addView(eliminar,5);
            actualizar.setText("Actualizar");
            eliminar.setText("Eliminar");
            recuperarCampos();
        }
        insertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertar_datos();
            }
        });
        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminar_dato();
            }
        });
        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualizar_dato();
            }
        });
    }


    private void recuperarCampos() {
        DocumentReference producto=baseDatos.collection("producto").document(clave);
        producto.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot resul=task.getResult();
                    String name=resul.get("nombre").toString();
                    int number=Integer.parseInt(resul.get("cantidad").toString());
                    float price=Float.parseFloat(resul.get("precio").toString());
                    nombre.setText(name);
                    precio.setText(price+"");
                    cantidad.setText(number+"");
                    datos.put("nombre",name);
                    datos.put("cantidad",number);
                    datos.put("precio",price);
                    datos.put("id",resul.getId());
                }
                else{
                    mensaje("Error al recuperar datos");
                }
            }
        });

    }

    private void eliminar_dato() {
        baseDatos.collection("producto").document(datos.get("id").toString()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mensaje("Se elimino correctamente");
                layo.removeView(eliminar);
                layo.removeView(actualizar);
                layo.addView(insertar,4);
                limpiarCampos();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mensaje("Error al eliminar");
            }
        });
    }

    private void insertar_datos() {
        baseDatos.collection("producto").add(obtenerCampos()).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                mensaje("Se ha insertado con exito");
                limpiarCampos();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mensaje("Error al insertar");
            }
        });
    }

    private void limpiarCampos() {
        nombre.setText("");
        cantidad.setText("");
        precio.setText("");
    }

    private void actualizar_dato() {
        baseDatos.collection("producto").document(datos.get("id").toString()).update(obtenerCampos()).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mensaje("Error al actualizar");
                layo.removeView(eliminar);
                layo.removeView(actualizar);
                layo.addView(insertar,4);
                limpiarCampos();
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mensaje("Se actualizo correctamente");
                limpiarCampos();
                startActivity(new Intent(MainActivity.this,Main3Activity.class));
                finish();
            }
        });
    }


    private void mensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }

    private Map<String,Object> obtenerCampos(){
        Map<String,Object> data=new HashMap<>();
        data.put("nombre",nombre.getText().toString());
        data.put("cantidad",Integer.parseInt(cantidad.getText().toString()));
        data.put("precio",Float.parseFloat(precio.getText().toString()));
        return data;
    }


}
