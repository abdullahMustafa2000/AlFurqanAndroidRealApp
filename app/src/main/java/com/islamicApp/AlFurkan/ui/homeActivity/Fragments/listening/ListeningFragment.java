package com.islamicApp.AlFurkan.ui.homeActivity.Fragments.listening;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.islamicApp.AlFurkan.Modules.RecitersItem;
import com.islamicApp.AlFurkan.ui.quranListenActivity.activity.QuranListenActivity;
import com.islamicApp.AlFurkan.ui.homeActivity.Fragments.listening.onlineQuran.ListeningSwarAdapter;
import com.islamicApp.AlFurkan.ui.homeActivity.Fragments.listening.onlineQuran.RecitersSpinnerAdapter;
import com.islamicApp.AlFurkan.Classes.RecitersDataBaseOpener;
import com.islamicApp.AlFurkan.Classes.InternetConnection;
import com.islamicApp.AlFurkan.ui.quranListenActivity.mpClass.QuranMediaPlayer;
import com.islamicApp.AlFurkan.Modules.OnlineSwarListen;
import com.islamicApp.AlFurkan.R;
import com.islamicApp.AlFurkan.ui.azkarListenActivity.service.AzkarListenService;
import com.islamicApp.AlFurkan.ui.quranListenActivity.service.QuranService;
import com.islamicApp.AlFurkan.ui.quranSuggestions.service.QuranSuggestionsService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListeningFragment extends Fragment implements View.OnClickListener {

    protected Spinner recitersSpinner;
    protected RecyclerView swarListenRecyclerview;
    protected ProgressBar loadingProgressBar;
    protected Button retryInternetConnectBtn;
    protected LinearLayout connectionLo;
    private ListeningSwarAdapter listeningSwarAdapter;
    SharedPreferences preferences, servicePref;
    private String QAREE_POS = "qareePos";
    private String SURA_POS = "suraPos";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater
                
                .inflate(R.layout.listening_fragment, container, false);
        preferences = getActivity().getSharedPreferences("listinigFragment", Context.MODE_PRIVATE);
        servicePref = getActivity().getSharedPreferences("quranService", Context.MODE_PRIVATE);
        this.getActivity().registerReceiver(receiver, new IntentFilter("Online_Track"));
        initView(view);
        setUpView();
        return view;
    }

    private void setUpView() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        RecitersDataBaseOpener recitersDataBaseOpener = new RecitersDataBaseOpener(getActivity());
        final List<RecitersItem> recitersItems = recitersDataBaseOpener.getAllReciters();
        RecitersSpinnerAdapter recitersSpinnerAdapter = new RecitersSpinnerAdapter(recitersItems, getActivity());
        recitersSpinner.setAdapter(recitersSpinnerAdapter);
        Intent intent = getActivity().getIntent();
        if (intent.getStringExtra("qareeLink") != null) {
            for (int i=0; i<recitersItems.size(); i++) {
                if (recitersItems.get(i).getServer().equals(intent.getStringExtra("qareeLink"))) {
                    recitersSpinner.setSelection(i);
                    break;
                }
            }
        } else
            recitersSpinner.setSelection(preferences.getInt(QAREE_POS, 0));
        loadingProgressBar.setVisibility(View.GONE);
        connectionLo.setVisibility(View.GONE);
        setRecyclerviewData(recitersItems.get(preferences.getInt(QAREE_POS, 0)));
        recitersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int pos = parent.getSelectedItemPosition();
                setRecyclerviewData(recitersItems.get(pos));
                preferences.edit().putInt(QAREE_POS, pos).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setRecyclerviewData(final RecitersItem recitersItem) {
        listeningSwarAdapter = new ListeningSwarAdapter(getActivity());
        listeningSwarAdapter.notifyChanges(getRecordedSuras(recitersItem));
        swarListenRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (QuranMediaPlayer.isInitialized()) {
            listeningSwarAdapter.setSuraLink(servicePref.getString("suraLink", ""));
            listeningSwarAdapter.notifyDataSetChanged();
        }
        swarListenRecyclerview.setAdapter(listeningSwarAdapter);
        setRecyclerviewAnim(swarListenRecyclerview);
        listeningSwarAdapter.setOnClicked(new ListeningSwarAdapter.OnClicked() {
            @Override
            public void onsuraClick(int position, OnlineSwarListen onlineSwarListen) {
                if (new InternetConnection(getActivity()).internetIsConnected()) {
                    stopOthers();
                    startNewSound(recitersItem, onlineSwarListen);
                    listeningSwarAdapter.setSuraLink(onlineSwarListen.getSuraLink());
                    listeningSwarAdapter.notifyDataSetChanged();
                    preferences.edit().putInt(SURA_POS, position).apply();
                    startMPActivity(onlineSwarListen);
                    for (int i = 0; i < suraModelArray.size(); i++)
                        preferences.edit().putString(i + "", suraModelArray.get(i).getSuraLink()).apply();
                    } else {
                    Toast.makeText(getActivity(), "يرجى الاتصال بالانترنت", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSuraLongClick(int position, OnlineSwarListen onlineSwarListen) {

            }
        });
    }

    public void stopOthers() {
        getActivity().startService(new Intent(getActivity(),
                AzkarListenService.class).putExtra("azkar_action", "stop"));

        getActivity().startService(new Intent(getActivity(),
                QuranSuggestionsService.class).putExtra("actionToken", "stop"));
    }

    private void startMPActivity(OnlineSwarListen onlineSwarListen) {
        startActivity(new Intent(getActivity(), QuranListenActivity.class)
                .putExtra("qareeName", onlineSwarListen.getQareeName())
                .putExtra("suraName", onlineSwarListen.getSuraName())
                .putStringArrayListExtra("surasArray", names));
        getActivity().finish();
    }

    ArrayList<OnlineSwarListen> suraModelArray = new ArrayList<>();
    final ArrayList<String> names = new ArrayList<>();

    private ArrayList<OnlineSwarListen> getRecordedSuras(RecitersItem recitersItem) {
        final ArrayList<String> surasArray = new ArrayList<>();
        String[] qrnName = getActivity().getResources().getStringArray(R.array.swar_names);
        surasArray.addAll(Arrays.asList(qrnName).subList(0, qrnName.length - 1));
        String[] strArray = recitersItem.getSuras().split(",");
        if (names.size() != 0)
            names.clear();
        if (suraModelArray.size() != 0)
            suraModelArray.clear();
        for (String s : strArray) {
            OnlineSwarListen suraModel = new OnlineSwarListen();
            names.add(qrnName[Integer.parseInt(s) - 1]);
            suraModel.setSuraName(qrnName[Integer.parseInt(s) - 1]);
            if ((surasArray.indexOf(qrnName[Integer.parseInt(s) - 1]) + 1) >= 100)
                suraModel.setSuraLink(recitersItem.getServer() + "/" + (surasArray.indexOf(qrnName[Integer.parseInt(s) - 1]) + 1) + ".mp3");
            else if ((surasArray.indexOf(qrnName[Integer.parseInt(s) - 1]) + 1) < 100 && (surasArray.indexOf(qrnName[Integer.parseInt(s) - 1]) + 1) > 10)
                suraModel.setSuraLink(recitersItem.getServer() + "/0" + (surasArray.indexOf(qrnName[Integer.parseInt(s) - 1]) + 1) + ".mp3");
            else
                suraModel.setSuraLink(recitersItem.getServer() + "/00" + (surasArray.indexOf(qrnName[Integer.parseInt(s) - 1]) + 1) + ".mp3");
            suraModel.setSuraPos(surasArray.indexOf(qrnName[Integer.parseInt(s) - 1]));
            suraModel.setQareeName(recitersItem.getName());
            suraModel.setQareeLink(recitersItem.getServer());
            suraModelArray.add(suraModel);
        }
        return suraModelArray;
    }

    public void startNewSound(RecitersItem recitersItem, OnlineSwarListen swarListen) {
        Intent serviceIntent = new Intent(getActivity(), QuranService.class);
        serviceIntent.putExtra("action_token", "startSura");
        serviceIntent.putExtra("suraName", swarListen.getSuraName());
        serviceIntent.putExtra("suraLink", swarListen.getSuraLink());
        serviceIntent.putExtra("qareeName", recitersItem.getName());
        serviceIntent.putExtra("qareeLink", recitersItem.getServer());
        serviceIntent.putExtra("recordedSuras", names);
        getActivity().startService(serviceIntent);
    }

    private void initView(View rootView) {
        recitersSpinner = (Spinner) rootView.findViewById(R.id.reciters_spinner);
        swarListenRecyclerview = (RecyclerView) rootView.findViewById(R.id.swar_listen_recyclerview);
        loadingProgressBar = (ProgressBar) rootView.findViewById(R.id.loading_progressBar);
        retryInternetConnectBtn = (Button) rootView.findViewById(R.id.retry_internet_connect_btn);
        retryInternetConnectBtn.setOnClickListener(ListeningFragment.this);
        connectionLo = (LinearLayout) rootView.findViewById(R.id.connection_lo);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = intent.getStringExtra("onlineAction");
            if (status != null && (status.equals("SuraFinished") || status.equals("play_next_sura"))) {
                selectNext();
            } else if (status != null && status.equals("play_previous_sura")) {
                selectPrev();
            } else if (status != null && status.equals("closesura")) {
                listeningSwarAdapter.setSuraLink("");
                listeningSwarAdapter.notifyDataSetChanged();
            }
        }
    };

    private void selectPrev() {
        int currentPos = preferences.getInt(SURA_POS, -1);
        currentPos--;
        if (currentPos < 0) {
            currentPos = 0;
        }
        listeningSwarAdapter.setSuraLink(preferences.getString(currentPos + "", ""));
        listeningSwarAdapter.notifyDataSetChanged();
        preferences.edit().putInt(SURA_POS, currentPos).apply();
    }

    private void selectNext() {
        int currentPos = preferences.getInt(SURA_POS, -1);
        currentPos++;
        if (currentPos > listeningSwarAdapter.getItemCount() - 1) {
            currentPos = 0;
        }
        listeningSwarAdapter.setSuraLink(preferences.getString(currentPos + "", ""));
        listeningSwarAdapter.notifyDataSetChanged();
        preferences.edit().putInt(SURA_POS, currentPos).apply();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            getActivity().unregisterReceiver(receiver);
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.retry_internet_connect_btn) {
            setUpView();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        this.getActivity().registerReceiver(receiver, new IntentFilter("Online_Track"));
        setUpView();
    }

    public void setRecyclerviewAnim (RecyclerView recyclerview) {
        Context context = recyclerview.getContext();
        LayoutAnimationController animationController =
                AnimationUtils.loadLayoutAnimation(context, R.anim.quran_layout_anim);
        recyclerview.setLayoutAnimation(animationController);
        listeningSwarAdapter.notifyDataSetChanged();
        recyclerview.scheduleLayoutAnimation();
    }
}
