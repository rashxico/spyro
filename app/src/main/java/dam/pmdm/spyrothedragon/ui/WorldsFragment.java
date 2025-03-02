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
import dam.pmdm.spyrothedragon.adapters.WorldsAdapter;
import dam.pmdm.spyrothedragon.databinding.FragmentWorldsBinding;

import dam.pmdm.spyrothedragon.models.World;
import dam.pmdm.spyrothedragon.databinding.Principal2Binding;
public class WorldsFragment extends Fragment {
    TextView infoBubble;
    private FragmentWorldsBinding binding;
    private RecyclerView recyclerView;
    private WorldsAdapter adapter;
    private List<World> worldsList;
    private Principal2Binding guideBinding;
    private Listener guideOverlayListener;  // Referencia a MainActivity


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Listener) {
            guideOverlayListener = (Listener) context;
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentWorldsBinding.inflate(inflater, container, false);
        guideBinding = Principal2Binding.inflate(inflater, container, false);

        // Añade guideBinding a la vista principal
        recyclerView = binding.recyclerViewWorlds;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        worldsList = new ArrayList<>();
        adapter = new WorldsAdapter(worldsList);
        recyclerView.setAdapter(adapter);

        loadWorlds();
     showInitialOverlay();

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loadWorlds() {
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.worlds);
            // Crear un parser XML
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, null);
            int eventType = parser.getEventType();
            World currentWorld = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = null;
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tagName = parser.getName();

                        if ("world".equals(tagName)) {
                            currentWorld = new World();
                        } else if (currentWorld != null) {
                            if ("name".equals(tagName)) {
                                currentWorld.setName(parser.nextText());
                            } else if ("description".equals(tagName)) {
                                currentWorld.setDescription(parser.nextText());
                            } else if ("image".equals(tagName)) {
                                currentWorld.setImage(parser.nextText());
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        tagName = parser.getName();

                        if ("world".equals(tagName) && currentWorld != null) {
                            worldsList.add(currentWorld);
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
            guideOverlayListener.Circle(R.id.nav_worlds);
        }
    }

}
