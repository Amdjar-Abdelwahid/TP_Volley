package projet.fst.ma.projetws;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import projet.fst.ma.projetws.adapters.EtudiantAdapter;
import projet.fst.ma.projetws.beans.Etudiant;

public class ListEtudiants extends AppCompatActivity {
    private static final String TAG = "ListEtudiants";

    private RecyclerView recyclerView;
    private EtudiantAdapter adapter;
    private ArrayList<Etudiant> etudiants;

    private RequestQueue requestQueue;

    private final String selectUrl = "http://10.0.2.2/AppMobile/Tp_Volley/web_services_php/ws/loadEtudiant.php";

    public static final int EDIT_STUDENT_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_etudiants);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        etudiants = new ArrayList<>();
        adapter = new EtudiantAdapter(etudiants);
        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);

        loadEtudiants();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_STUDENT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Recharger la liste après modification
            loadEtudiants();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Rafraîchir les données à chaque retour sur l'activité
        refreshData();
    }

    private void refreshData() {
        etudiants.clear();
        adapter.notifyDataSetChanged();
        loadEtudiants();
    }

    public void loadEtudiants() {
        Log.d(TAG, "Tentative de connexion à " + selectUrl);

        // Utilisons POST puisque c'est ce qui fonctionne dans Postman
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.POST, // Assurez-vous que c'est bien POST
                selectUrl,
                null, // Pas de JSONObject à envoyer dans le corps
                response -> {
                    Log.d(TAG, "Réponse reçue: " + response.toString());
                    try {
                        etudiants.clear(); // éviter doublons si on recharge

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject etudiantJson = response.getJSONObject(i);

                            int id = etudiantJson.getInt("id");
                            String nom = etudiantJson.getString("nom");
                            String prenom = etudiantJson.getString("prenom");
                            String ville = etudiantJson.getString("ville");
                            String sexe = etudiantJson.getString("sexe");

                            Etudiant etudiant = new Etudiant(id, nom, prenom, ville, sexe);
                            etudiants.add(etudiant);

                            Log.d(TAG, "Étudiant ajouté: " + etudiant);
                        }

                        adapter.notifyDataSetChanged();
                        Log.d(TAG, "Nombre total d'étudiants: " + etudiants.size());

                        if (etudiants.size() == 0) {
                            Log.d(TAG, "Aucun étudiant trouvé dans la réponse");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Erreur de parsing JSON: " + e.getMessage(), e);
                    }
                },
                error -> {
                    Log.e(TAG, "Erreur Volley: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.e(TAG, "Code d'erreur: " + error.networkResponse.statusCode);
                        Log.e(TAG, "Contenu de la réponse: " + new String(error.networkResponse.data));
                    } else {
                        Log.e(TAG, "Pas de réponse réseau", error);
                    }
                }
        ) {
            // Cette méthode override est importante pour les requêtes POST
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }

            // Au cas où votre API attend des paramètres même vides
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // Ajoutez des paramètres si nécessaire pour votre API POST
                return params;
            }
        };

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000, // 30 secondes
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(jsonArrayRequest);
    }
}
