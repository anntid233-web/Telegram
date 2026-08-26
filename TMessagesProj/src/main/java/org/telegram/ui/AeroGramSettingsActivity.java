package org.telegram.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;

/**
 * Главный экран раздела "AeroGram" в настройках.
 * 4 категории + блок ссылок.
 */
public class AeroGramSettingsActivity extends BaseFragment {

    private RecyclerListView listView;
    private ListAdapter listAdapter;

    // ---- row indices ----
    private int headerRow;
    private int versionRow;
    private int categoriesHeaderRow;
    private int ghostModeRow;
    private int spyModeRow;
    private int filtersRow;
    private int customizationRow;
    private int categoriesShadowRow;
    private int linksHeaderRow;
    private int channelRow;
    private int chatRow;
    private int translationRow;
    private int docsRow;
    private int linksShadowRow;
    private int rowCount;

    private void updateRowsId() {
        rowCount = 0;
        headerRow = rowCount++;
        versionRow = rowCount++;

        categoriesHeaderRow = rowCount++;
        ghostModeRow = rowCount++;
        spyModeRow = rowCount++;
        filtersRow = rowCount++;
        customizationRow = rowCount++;
        categoriesShadowRow = rowCount++;

        linksHeaderRow = rowCount++;
        channelRow = rowCount++;
        chatRow = rowCount++;
        translationRow = rowCount++;
        docsRow = rowCount++;
        linksShadowRow = rowCount++;
    }

    @Override
    public boolean onFragmentCreate() {
        updateRowsId();
        return super.onFragmentCreate();
    }

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle("AeroGram");
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
            if (position == ghostModeRow) {
                presentFragment(new GhostModeSettingsActivity());
            } else if (position == spyModeRow) {
                presentFragment(new SpyModeSettingsActivity());
            } else if (position == filtersRow) {
                presentFragment(new MessageFiltersActivity());
            } else if (position == customizationRow) {
                presentFragment(new CustomizationSettingsActivity());
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
            return position == ghostModeRow || position == spyModeRow
                    || position == filtersRow || position == customizationRow
                    || position == channelRow || position == chatRow
                    || position == translationRow || position == docsRow;
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
                    view = new TextCell(mContext);
                    break;
                case 2:
                    view = new ShadowSectionCell(mContext);
                    break;
                case 3:
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
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == categoriesHeaderRow) {
                        headerCell.setText("Категории");
                    } else if (position == linksHeaderRow) {
                        headerCell.setText("Ссылки");
                    }
                    break;
                }
                case 1: {
                    TextCell textCell = (TextCell) holder.itemView;
                    if (position == ghostModeRow) {
                        textCell.setTextAndIcon("Режим Призрака", R.drawable.msg_settings, true);
                    } else if (position == spyModeRow) {
                        textCell.setTextAndIcon("Шпион", R.drawable.msg_settings, true);
                    } else if (position == filtersRow) {
                        textCell.setTextAndIcon("Фильтры", R.drawable.msg_settings, true);
                    } else if (position == customizationRow) {
                        textCell.setTextAndIcon("Кастомизация", R.drawable.msg_settings, false);
                    } else if (position == channelRow) {
                        textCell.setTextAndValue("Канал", "@aerogram", true);
                    } else if (position == chatRow) {
                        textCell.setTextAndValue("Чаты", "@aerogramchat", true);
                    } else if (position == translationRow) {
                        textCell.setTextAndValue("Перевод", "Crowdin", true);
                    } else if (position == docsRow) {
                        textCell.setTextAndValue("Документация", "aerogram.dev", false);
                    }
                    break;
                }
                case 2: {
                    break;
                }
                case 3: {
                    TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == versionRow) {
                        cell.setText("AeroGram · " + BuildVars.BUILD_VERSION_STRING);
                    }
                    break;
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == categoriesHeaderRow || position == linksHeaderRow) {
                return 0;
            } else if (position == ghostModeRow || position == spyModeRow
                    || position == filtersRow || position == customizationRow
                    || position == channelRow || position == chatRow
                    || position == translationRow || position == docsRow) {
                return 1;
            } else if (position == categoriesShadowRow || position == linksShadowRow) {
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
