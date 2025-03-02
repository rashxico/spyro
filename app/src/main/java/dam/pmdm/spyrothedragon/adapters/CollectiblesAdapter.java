package dam.pmdm.spyrothedragon.adapters;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dam.pmdm.spyrothedragon.R;
import dam.pmdm.spyrothedragon.models.Collectible;

public class CollectiblesAdapter extends RecyclerView.Adapter<CollectiblesAdapter.CollectiblesViewHolder> {

    private List<Collectible> list;

    public CollectiblesAdapter(List<Collectible> collectibleList) {
        this.list = collectibleList;
    }

    @Override
    public CollectiblesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        return new CollectiblesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CollectiblesViewHolder holder, int position) {
        Collectible collectible = list.get(position);
        holder.nameTextView.setText(collectible.getName());

        // Cargar la imagen (simulado con un recurso drawable)
        int imageResId = holder.itemView.getContext().getResources().getIdentifier(collectible.getImage(), "drawable", holder.itemView.getContext().getPackageName());
        holder.imageImageView.setImageResource(imageResId);

        // Verificar si es la segunda imagen (posición 1)
        if (position == 1) {
            holder.itemView.setOnClickListener(v -> {
                // Incrementar el contador de clics
                holder.clickCount++;

                // Verificar si se han hecho 4 clics
                if (holder.clickCount == 4) {
                    // Ejecutar la acción deseada
                    performAction(holder.itemView.getContext());
                    holder.clickCount = 0; // Reiniciar el contador después de la acción
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // Método para realizar la acción deseada
    private void performAction(Context context) {
        // Mostrar un mensaje Toast
        Toast.makeText(context, "¡Has pulsado 4 veces en la gema!", Toast.LENGTH_SHORT).show();

        // Mostrar un diálogo con el video
        showVideoDialog(context);
    }

    //   Método para mostrar un diálogo con el video
    private void showVideoDialog(Context context) {
        // Crear un diálogo
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_video);

        // Obtener la referencia del SurfaceView y el botón de cerrar
        SurfaceView surfaceView = dialog.findViewById(R.id.surfaceView);
        Button btnClose = dialog.findViewById(R.id.btnClose);

        // Configurar el MediaPlayer
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            // Configurar la fuente del video (desde res/raw)
            Uri videoUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.videoplayback);
            mediaPlayer.setDataSource(context, videoUri);

            // Configurar el SurfaceHolder para renderizar el video
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    mediaPlayer.setDisplay(holder); // Asignar el SurfaceHolder al MediaPlayer

                    // Ajustar la relación de aspecto del video
                    mediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);

                    try {
                        mediaPlayer.prepare(); // Preparar el MediaPlayer
                        mediaPlayer.start(); // Iniciar la reproducción del video
                        mediaPlayer.setOnCompletionListener(mp -> {
                            dialog.dismiss(); // Cerrar el diálogo cuando el video termine
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    // No es necesario implementar esto
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    // Liberar recursos cuando la superficie se destruya
                    if (mediaPlayer != null) {
                        mediaPlayer.release();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Configurar el botón de cerrar
        btnClose.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                mediaPlayer.release(); // Liberar recursos del MediaPlayer
            }
            dialog.dismiss(); // Cerrar el diálogo
        });

        // Mostrar el diálogo
        dialog.show();

        // Ajustar el tamaño del diálogo
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    public static class CollectiblesViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        ImageView imageImageView;
        int clickCount = 0; // Contador de clics

        public CollectiblesViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name);
            imageImageView = itemView.findViewById(R.id.image);
        }
    }
}