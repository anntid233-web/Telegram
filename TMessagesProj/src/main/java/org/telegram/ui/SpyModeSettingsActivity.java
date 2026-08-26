package org.telegram.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;

/**
 * Экран "Шпион" — сохранение удалённых сообщений, истории правок, вложений и т.д.
 */
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

        listAdapter = new ListAdapter(context);
        fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) fragmentView;

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
                // открыть выбор папки для вложений (стандартный DocumentsUI / кастомный пикер)
            } else if (position == exportDbRow) {
                // экспорт локальной базы (например, через стандартный ACTION_CREATE_DOCUMENT)
            } else if (position == importDbRow) {
                // импорт локальной базы (ACTION_OPEN_DOCUMENT)
            } else if (position == clearRow) {
                // диалог подтверждения очистки локальной базы шпиона
            }
        });

        return fragmentView;
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        private Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position != headerRow && position != infoReadDateRow && position != infoLastOnlineRow;
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
                        cell.setTextAndValueAndCheck("Сохранять вложения", "Настроить чаты и лимиты",
                                pref("spy_save_attachments", true), true, false);
                    }
                    break;
                }
                case 2: {
                    TextCell cell = (TextCell) holder.itemView;
                    if (position == attachmentsFolderRow) {
                        cell.setTextAndValue("Папка вложений", "Saved Attachments", true);
                    } else if (position == exportDbRow) {
                        cell.setTextAndIcon("Экспорт базы данных", R.drawable.msg_shareout, true);
                    } else if (position == importDbRow) {
                        cell.setTextAndIcon("Импорт базы данных", R.drawable.msg_shareout, true);
                    } else if (position == clearRow) {
                        cell.setTextAndIcon("Очистить", R.drawable.msg_delete, false);
                        cell.setColors(0xffcf3030, 0xffcf3030);
                    }
                    break;
                }
                case 3: {
                    TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == infoReadDateRow) {
                        cell.setText("Локально сохраняет данные о чтении сообщений. Будет использоваться, если Telegram не предоставит дату чтения.");
                    } else if (position == infoLastOnlineRow) {
                        cell.setText("Сохраняет последний известный онлайн для людей со скрытым последним посещением. Вы сможете очень приблизительно увидеть, когда они были последний раз онлайн.");
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
