package com.example.notecook.Fragement;

import static org.chromium.base.ThreadUtils.runOnUiThread;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.notecook.R;
import com.example.notecook.databinding.FragmentChatBinding;

import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class Frg_chat extends Fragment {


    private Socket socket;
    private ListView messagesView;
    private EditText messageInput;
    private Button sendButton;
    private ArrayAdapter<String> messagesAdapter;
    private ArrayList<String> messagesList;
    private FragmentChatBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater, container, false);
        messagesView = binding.messagesView;
        messageInput = binding.messageInput;
        sendButton = binding.sendButton;

        messagesList = new ArrayList<>();
        messagesAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, messagesList);
        messagesView.setAdapter(messagesAdapter);

        try {
            socket = IO.socket("http://YOUR_SERVER_IP:3000");
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        socket.on("chat message", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String message = (String) args[0];
                        messagesList.add(message);
                        messagesAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageInput.getText().toString();
                if (!message.isEmpty()) {
                    socket.emit("chat message", message);
                    messageInput.setText("");
                    messagesList.add(message);
                    messagesAdapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(getContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return binding.getRoot();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        socket.disconnect();
        socket.off("chat message");
    }
}