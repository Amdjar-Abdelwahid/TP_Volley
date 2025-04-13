package projet.fst.ma.projetws;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditEtudiantActivity extends AppCompatActivity {
    private EditText etNom, etPrenom, etVille;
    private RadioButton rbHomme, rbFemme;
    private Button btnSave;

    private int etudiantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_etudiant);

        // Initialiser les vues
        etNom = findViewById(R.id.et_nom);
        etPrenom = findViewById(R.id.et_prenom);
        etVille = findViewById(R.id.et_ville);
        rbHomme = findViewById(R.id.rb_homme);
        rbFemme = findViewById(R.id.rb_femme);
        btnSave = findViewById(R.id.btn_save);

        // Récupérer les données de l'intent
        Intent intent = getIntent();
        etudiantId = intent.getIntExtra("id", -1);
        etNom.setText(intent.getStringExtra("nom"));
        etPrenom.setText(intent.getStringExtra("prenom"));
        etVille.setText(intent.getStringExtra("ville"));

        String sexe = intent.getStringExtra("sexe");
        if ("homme".equalsIgnoreCase(sexe)) {
            rbHomme.setChecked(true);
        } else {
            rbFemme.setChecked(true);
        }

        btnSave.setOnClickListener(v -> updateEtudiant());
    }

    private void updateEtudiant() {
        String nom = etNom.getText().toString().trim();
        String prenom = etPrenom.getText().toString().trim();
        String ville = etVille.getText().toString().trim();
        String sexe = rbHomme.isChecked() ? "homme" : "femme";

        if (nom.isEmpty() || prenom.isEmpty() || ville.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2/TP%20Volley/ws/updateEtudiant.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");

                        if (success) {
                            Toast.makeText(this, "Étudiant mis à jour avec succès", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish(); // Fermer l'activité et retourner à la liste
                        } else {
                            Toast.makeText(this, "Échec de la mise à jour", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        finish();
                        //Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Erreur réseau: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(etudiantId));
                params.put("nom", nom);
                params.put("prenom", prenom);
                params.put("ville", ville);
                params.put("sexe", sexe);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}