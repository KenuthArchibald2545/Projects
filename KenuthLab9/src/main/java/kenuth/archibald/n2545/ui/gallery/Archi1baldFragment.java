package kenuth.archibald.n2545.ui.gallery;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import kenuth.archibald.n2545.R;

public class Archi1baldFragment extends Fragment {

    private ToggleButton fileType;
    private EditText fileName, fileContents;
    private TextView fileListTextView;
    private List<String> fileNames;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        fileName = root.findViewById(R.id.activity_internalstorage_filename);
        fileContents = root.findViewById(R.id.activity_internalstorage_filecontents);

        fileType = root.findViewById(R.id.activity_internalstorage_filetype);
        fileType.setChecked(true);

        root.findViewById(R.id.activity_internalstorage_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFile(requireContext(), fileType.isChecked());
            }
        });

        root.findViewById(R.id.activity_internalstorage_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFile(requireContext(), fileType.isChecked());
            }
        });

        root.findViewById(R.id.activity_internalstorage_write).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeFile(requireContext(), fileType.isChecked());
            }
        });

        root.findViewById(R.id.activity_internalstorage_read).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readFile(requireContext(), fileType.isChecked());
            }
        });

        return root;
    }

    private void onCreateFile(View view) {
        if (TextUtils.isEmpty(fileName.getText().toString())) {
            showSnackbar("Kenuth Archibald - File name missing");
            return;
        }
        createFile(requireContext(), fileType.isChecked());
    }

    private void onDeleteFile(View view) {
        if (TextUtils.isEmpty(fileName.getText().toString())) {
            showSnackbar("Kenuth Archibald - File name missing");
            return;
        }
        deleteFile(requireContext(), fileType.isChecked());
    }

    private void onWriteFile(View view) {
        if (TextUtils.isEmpty(fileName.getText().toString())) {
            showSnackbar("Kenuth Archibald - File name missing");
            return;
        }
        if (TextUtils.isEmpty(fileContents.getText().toString())) {
            Toast.makeText(requireContext(), "Kenuth Archibald - Content Missing", Toast.LENGTH_LONG).show();
            return;
        }
        writeFile(requireContext(), fileType.isChecked());
        fileContents.setText("");
    }

    private void onReadFile(View view) {
        if (TextUtils.isEmpty(fileName.getText().toString())) {
            showSnackbar("Kenuth Archibald - File name missing");
            return;
        }
        readFile(requireContext(), fileType.isChecked());
    }

    private void showSnackbar(String message) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_INDEFINITE).show();
    }

    private void createFile(Context context, boolean isPersistent) {
        File file;
        if (isPersistent) {
            file = new File(context.getFilesDir(), fileName.getText().toString());
        } else {
            file = new File(context.getCacheDir(), fileName.getText().toString());
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
                Toast.makeText(context, String.format("File %s has been created", fileName.getText().toString()), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(context, String.format("File %s creation failed", fileName.getText().toString()), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, String.format("File %s already exists", fileName.getText().toString()), Toast.LENGTH_SHORT).show();
        }
    }

    private void writeFile(Context context, boolean isPersistent) {
        try {
            FileOutputStream fileOutputStream;
            if (isPersistent) {
                fileOutputStream = context.openFileOutput(fileName.getText().toString(), Context.MODE_PRIVATE);
            } else {
                File file = new File(context.getCacheDir(), fileName.getText().toString());
                fileOutputStream = new FileOutputStream(file);
            }
            fileOutputStream.write(fileContents.getText().toString().getBytes(Charset.forName("UTF-8")));
            Toast.makeText(context, String.format("Write to %s successful", fileName.getText().toString()), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, String.format("Write to file %s failed", fileName.getText().toString()), Toast.LENGTH_SHORT).show();
        }
    }

    private void readFile(Context context, boolean isPersistent) {
        try {
            FileInputStream fileInputStream;
            if (isPersistent) {
                fileInputStream = context.openFileInput(fileName.getText().toString());
            } else {
                File file = new File(context.getCacheDir(), fileName.getText().toString());
                fileInputStream = new FileInputStream(file);
            }

            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, Charset.forName("UTF-8"));
            List<String> lines = new ArrayList<>();
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
            fileContents.setText(TextUtils.join("\n", lines));
            Toast.makeText(context, String.format("Read from file %s successful", fileName.getText().toString()), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, String.format("Read from file %s failed", fileName.getText().toString()), Toast.LENGTH_SHORT).show();
            fileContents.setText("");

        }
    }

    private void deleteFile(Context context, boolean isPersistent) {
        File file;
        if (isPersistent) {
            file = new File(context.getFilesDir(), fileName.getText().toString());
        } else {
            file = new File(context.getCacheDir(), fileName.getText().toString());
        }
        if (file.exists()) {
            file.delete();
            Toast.makeText(context, String.format("File %s has been deleted", fileName.getText().toString()), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, String.format("File %s doesn't exist", fileName.getText().toString()), Toast.LENGTH_SHORT).show();
        }
    }

    private void limitFileCount(Context context, boolean isPersistent) {
        if (fileNames.size() > 3) {
            String oldestFileName = fileNames.remove(0);
            File file = new File(isPersistent ? context.getFilesDir() : context.getCacheDir(), oldestFileName);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    private void updateFileListDisplay() {
        fileListTextView.setText("");
        for (String name : fileNames) {
            fileListTextView.append(name + "\n");
        }
    }

}
