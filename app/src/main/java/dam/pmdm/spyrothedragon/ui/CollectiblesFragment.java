package dam.pmdm.spyrothedragon.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import dam.pmdm.spyrothedragon.R;
import dam.pmdm.spyrothedragon.adapters.CollectiblesAdapter;
import dam.pmdm.spyrothedragon.databinding.FragmentCollectiblesBinding;
import dam.pmdm.spyrothedragon.models.Collectible;
import dam.pmdm.spyrothedragon.databinding.Principal3Binding;
public class CollectiblesFragment extends Fragment {
    private View bocadillo;

    private FragmentCollectiblesBinding binding;
    private RecyclerView recyclerView;
    private CollectiblesAdapter adapter;
    private List<Collectible> collectiblesList;
    private dam.pmdm.spyrothedragon.databinding.Principal3Binding guideBinding;
    private Listener guideOverlayListener;
    private TextView Bocadillo;
    public interface BocadilloVisibilityListener {
        void hideBocadillo();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Listener) {
            guideOverlayListener = (Listener) context;
        }
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCollectiblesBinding.inflate(inflater, container, false);
        guideBinding = Principal3Binding.inflate(inflater, container, false);

        // Añade guideBinding a la vista principal

        recyclerView = binding.recyclerViewCollectibles;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        collectiblesList = new ArrayList<>();
        adapter = new CollectiblesAdapter(collectiblesList);
        recyclerView.setAdapter(adapter);

        loadCollectibles();
        showInitialOverlay();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loadCollectibles() {
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.collectibles);

            // Crear un parser XML
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, null);

            int eventType = parser.getEventType();
            Collectible currentCollectible = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = null;

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tagName = parser.getName();

                        if ("collectible".equals(tagName)) {
                            currentCollectible = new Collectible();
                        } else if (currentCollectible != null) {
                            if ("name".equals(tagName)) {
                                currentCollectible.setName(parser.nextText());
                            } else if ("description".equals(tagName)) {
                                currentCollectible.setDescription(parser.nextText());
                            } else if ("image".equals(tagName)) {
                                currentCollectible.setImage(parser.nextText());
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        tagName = parser.getName();

                        if ("collectible".equals(tagName) && currentCollectible != null) {
                            collectiblesList.add(currentCollectible);
                        }
                        break;
                }

                eventType = parser.next();
            }

            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showInitialOverlay() {
        View rootView = binding.getRoot();
        ((ViewGroup) rootView).addView(guideBinding.getRoot());
        // Asegúrate de que guideBinding esté correctamente inicializado
        if (guideBinding == null) {
            Log.e("WorldsFragment", "guideBinding is null");
            return;
        }
        if (guideOverlayListener != null) {
            guideOverlayListener.Circle(R.id.nav_collectibles);
        }
        // Configura el botón de siguiente en la guía inicial


        // Muestra el mensaje de la burbuja informativa
    }
    public void hideBocadillo() {
        if (Bocadillo != null) {
            Bocadillo.setVisibility(View.GONE);
        }
    }
}
