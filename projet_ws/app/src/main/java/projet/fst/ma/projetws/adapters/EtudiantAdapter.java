package projet.fst.ma.projetws.adapters;

import static projet.fst.ma.projetws.ListEtudiants.EDIT_STUDENT_REQUEST_CODE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import projet.fst.ma.projetws.EditEtudiantActivity;
import projet.fst.ma.projetws.ListEtudiants;
import projet.fst.ma.projetws.R;
import projet.fst.ma.projetws.beans.Etudiant;

public class EtudiantAdapter extends RecyclerView.Adapter<EtudiantAdapter.EtudiantViewHolder> {
    private List<Etudiant> etudiants;
    private Context context;

    public EtudiantAdapter(List<Etudiant> etudiants) {
        this.etudiants = etudiants;
    }

    @NonNull
    @Override
    public EtudiantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_etudiant, parent, false);
        return new EtudiantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EtudiantViewHolder holder, int position) {
        Etudiant etudiant = etudiants.get(position);

        holder.tvNom.setText(etudiant.getNom() + " " + etudiant.getPrenom());
        holder.tvVille.setText(etudiant.getVille());
        holder.tvSexe.setText(etudiant.getSexe());

        // Bouton de suppression
        holder.btnDelete.setOnClickListener(v -> {
            deleteEtudiant(etudiant.getId(), position);
        });

        // Bouton de modification
        holder.btnEdit.setOnClickListener(v -> {
            // Ouvrir l'activité d'édition
            Intent intent = new Intent(context, EditEtudiantActivity.class);
            intent.putExtra("id", etudiant.getId());
            intent.putExtra("nom", etudiant.getNom());
            intent.putExtra("prenom", etudiant.getPrenom());
            intent.putExtra("ville", etudiant.getVille());
            intent.putExtra("sexe", etudiant.getSexe());

            if (context instanceof Activity) {
                ((Activity) context).startActivityForResult(intent, EDIT_STUDENT_REQUEST_CODE);
            } else {
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return etudiants.size();
    }

    private void deleteEtudiant(int id, final int position) {
        String url = "http://10.0.2.2/TP%20Volley/ws/deleteEtudiant.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");

                        if (success) {
                            // Suppression réussie, mettre à jour la liste
                            etudiants.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, etudiants.size());
                            Toast.makeText(context, "Étudiant supprimé avec succès", Toast.LENGTH_SHORT).show();

                            // Appeler la méthode load() de l'activité ListEtudiants
                            if (context instanceof ListEtudiants) {
                                ((ListEtudiants) context).loadEtudiants();
                            }
                        } else {
                            Toast.makeText(context, "Échec de la suppression", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ((ListEtudiants) context).loadEtudiants();
                       // Toast.makeText(context, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(context, "Erreur réseau: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(id));
                return params;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }

    public static class EtudiantViewHolder extends RecyclerView.ViewHolder {
        TextView tvNom, tvVille, tvSexe;
        ImageButton btnDelete, btnEdit;

        public EtudiantViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNom = itemView.findViewById(R.id.tv_nom);
            tvVille = itemView.findViewById(R.id.tv_ville);
            tvSexe = itemView.findViewById(R.id.tv_sexe);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnEdit = itemView.findViewById(R.id.btn_edit);
        }
    }
}