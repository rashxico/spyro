package dam.pmdm.spyrothedragon.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dam.pmdm.spyrothedragon.R;
import dam.pmdm.spyrothedragon.models.Character;
import dam.pmdm.spyrothedragon.ui.FireAnimationView;

public class CharactersAdapter extends RecyclerView.Adapter<CharactersAdapter.CharactersViewHolder> {
    private FireAnimationView animacionFuego; // Referencia a la animación de fuego

    private List<Character> list;

    public CharactersAdapter(List<Character> charactersList) {
        this.list = charactersList;
    }

    @Override
    public CharactersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        return new CharactersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CharactersViewHolder holder, int position) {
        Character character = list.get(position);
        holder.nameTextView.setText(character.getName());

        // Cargar la imagen (simulado con un recurso drawable)
        int imageResId = holder.itemView.getContext().getResources().getIdentifier(character.getImage(), "drawable", holder.itemView.getContext().getPackageName());
        holder.imageImageView.setImageResource(imageResId);

        // Verificar si es la segunda imagen (posición 1)
        if (position == 0) {
            // Configurar la pulsación prolongada
            holder.itemView.setOnLongClickListener(v -> {
                // Mostrar la animación de fuego
                showFireAnimation(holder.itemView.getContext(), holder.imageImageView);
                return true; // Indicar que el evento ha sido consumido
            });
        } else {
            // Configurar la pulsación corta);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // Método para mostrar la animación de fuego
    private void showFireAnimation(Context context, ImageView imageView) {
        // Crear una instancia de la vista de animación de fuego
        animacionFuego = new FireAnimationView(context);
        // Obtener las coordenadas de la imagen en la pantalla
        int[] location = new int[2];
        imageView.getLocationOnScreen(location);
        // Obtener el desplazamiento de la barra de estado
        int statusBarHeight = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }

        // Configurar el tamaño de la animación (mismo ancho que la imagen)
        int width = imageView.getWidth();
        int height = 300; // Altura de la animación de fuego
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width, height);
        animacionFuego.setLayoutParams(params);

        // Configurar la posición de la animación (debajo de la imagen)
        animacionFuego.setX(location[0]); // Posición X de la imagen
        animacionFuego.setY(location[1] + imageView.getHeight() - statusBarHeight-200); // Posición Y debajo de la imagen

        // Añadir la vista de animación al contenedor principal
        ViewGroup rootView = (ViewGroup) imageView.getRootView();
        rootView.addView(animacionFuego);

        // Iniciar la animación de fuego
        startFireAnimation(animacionFuego);
        Toast.makeText(context, "¡Has  descubierto el enfado de Spyro!", Toast.LENGTH_SHORT).show();
    }

    // Método para eliminar la animación de fuego
    public void eliminarAnimacionFuego() {
        if (animacionFuego != null) {
            ViewGroup rootView = (ViewGroup) animacionFuego.getParent();
            if (rootView != null) {
                rootView.removeView(animacionFuego); // Eliminar la vista de animación
            }
            animacionFuego = null; // Liberar la referencia
        }
    }


    // Método para iniciar la animación de fuego
    private void startFireAnimation(FireAnimationView fireAnimationView) {
        // Usar un ValueAnimator para animar la llama
        android.animation.ValueAnimator animator = android.animation.ValueAnimator.ofFloat(200, 0); // Rango de altura (de 200 a 0)
        animator.setDuration(1000); // Duración de la animación
        animator.setRepeatCount(android.animation.ValueAnimator.INFINITE); // Repetir indefinidamente
        animator.setRepeatMode(android.animation.ValueAnimator.REVERSE); // Invertir la animación

        // Actualizar la altura de la llama en cada fotograma
        animator.addUpdateListener(animation -> {
            float height = (float) animation.getAnimatedValue();
            fireAnimationView.setFlameHeight(height);
        });

        // Iniciar la animación
        animator.start();
    }

    public static class CharactersViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        ImageView imageImageView;

        public CharactersViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name);
            imageImageView = itemView.findViewById(R.id.image);
        }
    }
}