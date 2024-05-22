package com.joan.joan_tarea_4;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class VerDatosActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private ArrayList<Dato> listaDatos;
    private ArrayList<Dato> listaDatosCompleta;
    private AdaptadorDatos adaptador;
    private ListView listViewDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_datos);

        db = FirebaseFirestore.getInstance();
        listaDatos = new ArrayList<>();
        listaDatosCompleta = new ArrayList<>();
        adaptador = new AdaptadorDatos(this, listaDatos);

        listViewDatos = findViewById(R.id.listViewDatos);
        listViewDatos.setAdapter(adaptador);

        cargarDatosDesdeFirebase();

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrarDatos(newText);
                return false;
            }
        });
    }

    private void cargarDatosDesdeFirebase() {
        db.collection("usuarios").get().addOnSuccessListener(querySnapshot -> {
            listaDatos.clear();
            listaDatosCompleta.clear();
            for (QueryDocumentSnapshot document : querySnapshot) {
                Dato dato = document.toObject(Dato.class);
                listaDatos.add(dato);
                listaDatosCompleta.add(dato);
            }
            adaptador.notifyDataSetChanged();
        });
    }

    private void filtrarDatos(String texto) {
        ArrayList<Dato> listaFiltrada = new ArrayList<>();
        if (texto.isEmpty()) {
            listaFiltrada.addAll(listaDatosCompleta);
        } else {
            for (Dato dato : listaDatosCompleta) {
                if (dato.getPrimerNombre().toLowerCase().contains(texto.toLowerCase()) ||
                        dato.getApellido().toLowerCase().contains(texto.toLowerCase()) ||
                        String.valueOf(dato.getAñoNacimiento()).contains(texto)) {
                    listaFiltrada.add(dato);
                }
            }
        }

        if (listaFiltrada.isEmpty()) {
            Toast.makeText(this, "No se han encontrado coincidencias", Toast.LENGTH_SHORT).show();
        }

        adaptador.clear();
        adaptador.addAll(listaFiltrada);
        adaptador.notifyDataSetChanged();
    }
}
