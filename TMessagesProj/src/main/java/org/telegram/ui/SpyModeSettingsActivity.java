package org.telegram.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;

public class SpyModeSettingsActivity extends BaseFragment {

    private RecyclerListView listView;
    private ListAdapter listAdapter;
    private SharedPreferences prefs;

    private int headerRow;
    private int saveDeletedRow;
    private int saveEditsRow;
    private int saveInBotsRow;
    private int saveReadDateRow;
    private int infoReadDateRow;
    private int saveLastOnlineRow;
    private int infoLastOnlineRow;
    private int saveAttachmentsRow;
    private int attachmentsFolderRow;
    private int infoMaxFolderSizeRow;
    private int exportDbRow;
    private int importDbRow;
    private int clearRow;
    private int rowCount;

    private void updateRowsId() {
        rowCount = 0;
        headerRow = rowCount++;
        saveDeletedRow = rowCount++;
        saveEditsRow = rowCount++;
        saveInBotsRow = rowCount++;
        saveReadDateRow = rowCount++;
        infoReadDateRow = rowCount++;
        saveLastOnlineRow = rowCount++;
        infoLastOnlineRow = rowCount++;
        saveAttachmentsRow = rowCount++;
        attachmentsFolderRow = rowCount++;
        infoMaxFolderSizeRow = rowCount++;
        exportDbRow = rowCount++;
        importDbRow = rowCount++;
        clearRow = rowCount++;
    }

    @Override
    public boolean onFragmentCreate() {
        prefs = ApplicationLoader.applicationContext.getSharedPreferences("aerogram_config", Context.MODE_PRIVATE);
        updateRowsId();
        return super.onFragmentCreate();
    }

    private boolean pref(String key, boolean def) {
        return prefs.getBoolean(key, def);
    }

    private void togglePref(String key) {
        prefs.edit().putBoolean(key, !pref(key, false)).apply();
    }

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle("Шпион");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        listAdapter = new ListAdapter(context);
        fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));

        listView = new RecyclerListView(context);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listView.setAdapter(listAdapter);
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        listView.setOnItemClickListener((view, position) -> {
            if (position == saveDeletedRow) {
                togglePref("spy_save_deleted");
                listAdapter.notifyItemChanged(position);
            } else if (position == saveEditsRow) {
                togglePref("spy_save_edits");
                listAdapter.notifyItemChanged(position);
            } else if (position == saveInBotsRow) {
                togglePref("spy_save_in_bots");
                listAdapter.notifyItemChanged(position);
            } else if (position == saveReadDateRow) {
                togglePref("spy_save_read_date");
                listAdapter.notifyItemChanged(position);
            } else if (position == saveLastOnlineRow) {
                togglePref("spy_save_last_online");
                listAdapter.notifyItemChanged(position);
            } else if (position == saveAttachmentsRow) {
                togglePref("spy_save_attachments");
                listAdapter.notifyItemChanged(position);
            } else if (position == attachmentsFolderRow) {
                showAttachmentsFolderDialog();
            } else if (position == exportDbRow) {
                exportDatabase();
            } else if (position == importDbRow) {
                importDatabase();
            } else if (position == clearRow) {
                showClearConfirmDialog();
            }
        });

        return fragmentView;
    }

    private void showAttachmentsFolderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle("Папка вложений");
        CharSequence[] options = new CharSequence[]{"Saved Attachments", "Telegram Aerogram", "Внутреннее хранилище"};
        builder.setItems(options, (dialog, which) -> {
            prefs.edit().putInt("spy_attachments_folder_option", which).apply();
            listAdapter.notifyItemChanged(attachmentsFolderRow);
        });
        showDialog(builder.create());
    }

    private String attachmentsFolderText() {
        int option = prefs.getInt("spy_attachments_folder_option", 0);
        switch (option) {
            case 1:
                return "Telegram Aerogram";
            case 2:
                return "Внутреннее хранилище";
            default:
                return "Saved Attachments";
        }
    }

    private void exportDatabase() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle("Экспорт базы данных");
        builder.setMessage("Локальная база данных Шпиона будет сохранена в выбранную папку.");
        builder.setPositiveButton(LocaleController.getString(R.string.OK), null);
        showDialog(builder.create());
    }

    private void importDatabase() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle("Импорт базы данных");
        builder.setMessage("Выберите файл базы данных для импорта.");
        builder.setPositiveButton(LocaleController.getString(R.string.OK), null);
        showDialog(builder.create());
    }

    private void showClearConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle("Очистить");
        builder.setMessage("Вы уверены, что хотите удалить всю локальную базу данных Шпиона? Это действие необратимо.");
        builder.setPositiveButton("Очистить", (dialog, which) -> {
            prefs.edit().remove("spy_save_deleted").remove("spy_save_edits").apply();
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        showDialog(builder.create());
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        private Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position != headerRow && position != infoReadDateRow
                    && position != infoLastOnlineRow && position != infoMaxFolderSizeRow;
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new HeaderCell(mContext);
                    break;
                case 1:
                    view = new TextCheckCell(mContext);
                    break;
                case 2:
                    view = new TextCell(mContext);
                    break;
                default:
                    view = new TextInfoPrivacyCell(mContext);
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0: {
                    ((HeaderCell) holder.itemView).setText("Режим шпиона");
                    break;
                }
                case 1: {
                    TextCheckCell cell = (TextCheckCell) holder.itemView;
                    if (position == saveDeletedRow) {
                        cell.setTextAndCheck("Сохранять удалённые сообщения", pref("spy_save_deleted", true), true);
                    } else if (position == saveEditsRow) {
                        cell.setTextAndCheck("Сохранять историю правок", pref("spy_save_edits", true), true);
                    } else if (position == saveInBotsRow) {
                        cell.setTextAndCheck("Сохранять в чатах с ботами", pref("spy_save_in_bots", true), true);
                    } else if (position == saveReadDateRow) {
                        cell.setTextAndCheck("Сохранять дату чтения", pref("spy_save_read_date", false), true);
                    } else if (position == saveLastOnlineRow) {
                        cell.setTextAndCheck("Сохранять последний онлайн", pref("spy_save_last_online", false), true);
                    } else if (position == saveAttachmentsRow) {
                        cell.setTextAndValueAndCheck("Сохранять вложения", "Настроить чаты и лимиты", pref("spy_save_attachments", true), false, true);
                    }
                    break;
                }
                case 2: {
                    TextCell cell = (TextCell) holder.itemView;
                    if (position == attachmentsFolderRow) {
                        cell.setTextAndValue("Папка вложений", attachmentsFolderText(), true);
                    } else if (position == exportDbRow) {
                        cell.setTextAndIcon("Экспорт базы данных", R.drawable.msg_settings, true);
                    } else if (position == importDbRow) {
                        cell.setTextAndIcon("Импорт базы данных", R.drawable.msg_settings, true);
                    } else if (position == clearRow) {
                        cell.setTextAndIcon("Очистить", R.drawable.msg_settings, false);
                    }
                    break;
                }
                case 3: {
                    TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == infoReadDateRow) {
                        cell.setText("Локально сохраняет данные о чтении сообщений. Будет использоваться, если Telegram не предоставит дату чтения.");
                    } else if (position == infoLastOnlineRow) {
                        cell.setText("Сохраняет последний известный онлайн для людей со скрытым последним посещением. Вы сможете очень приблизительно увидеть, когда они были последний раз онлайн.");
                    } else if (position == infoMaxFolderSizeRow) {
                        cell.setText("Если размер папки превышает этот лимит, самые старые вложения будут удалены с устройства.");
                    }
                    break;
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == headerRow) {
                return 0;
            } else if (position == saveDeletedRow || position == saveEditsRow
                    || position == saveInBotsRow || position == saveReadDateRow
                    || position == saveLastOnlineRow || position == saveAttachmentsRow) {
                return 1;
            } else if (position == attachmentsFolderRow || position == exportDbRow
                    || position == importDbRow || position == clearRow) {
                return 2;
            } else {
                return 3;
            }
        }
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        return new ArrayList<>();
    }
}
