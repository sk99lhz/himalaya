package com.lhz.sk.himalaya.activitys;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lhz.sk.himalaya.R;
import com.lhz.sk.himalaya.adapters.AlbumListAdapter;
import com.lhz.sk.himalaya.adapters.SearchRecommendAdapter;
import com.lhz.sk.himalaya.bases.BaseActivity;
import com.lhz.sk.himalaya.interfaces.ISearchViewCallBack;
import com.lhz.sk.himalaya.presenters.DetailPresenter;
import com.lhz.sk.himalaya.presenters.SearchPresenter;
import com.lhz.sk.himalaya.utils.LogUtil;
import com.lhz.sk.himalaya.utils.ToastUtils;
import com.lhz.sk.himalaya.views.FlowTextLayout;
import com.lhz.sk.himalaya.views.UILoader;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity implements ISearchViewCallBack {

    private ImageView mBackBtn;
    private EditText mSearchEt;
    private TextView mSearchTv;
    private FrameLayout mContentfl;
    private SearchPresenter mSearchPresenter;
    private String TAB = "SearchActivity";
    private FlowTextLayout mTextLayout;
    private UILoader mUiLoader;
    private RecyclerView mSearchlist;
    private AlbumListAdapter mAlbumListAdapter;
    private InputMethodManager mInputMethodManager;
    private ImageView mDeleteIv;
    private RecyclerView mRecommendRv;
    private SearchRecommendAdapter mSearchRecommendAdapter;
    private TwinklingRefreshLayout mRefreshLayout;
    private boolean mNeedSugest = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //mInputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        initView();
        initEvent();
        mSearchPresenter = SearchPresenter.getInstance();
        mSearchPresenter.registerViewCallback(this);
        mSearchPresenter.getHotWord();
    }

    private void initEvent() {
        mDeleteIv.setVisibility(View.GONE);
        mSearchlist.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(SearchActivity.this, 3);
                outRect.bottom = UIUtil.dip2px(SearchActivity.this, 3);
                outRect.left = UIUtil.dip2px(SearchActivity.this, 5);
                outRect.right = UIUtil.dip2px(SearchActivity.this, 5);
            }
        });
        mBackBtn.setOnClickListener(v -> onBackPressed());
        mSearchTv.setOnClickListener(v -> {
            String data = mSearchEt.getText().toString().trim();
            if (mSearchPresenter != null) {
                if (TextUtils.isEmpty(data)){
                    ToastUtils.showToast(SearchActivity.this,"搜索内容不可以为空！");
                }else {
                    mSearchPresenter.Search(data);
                    mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
                }

            }
        });
        mSearchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    mSearchPresenter.getHotWord();
                    mDeleteIv.setVisibility(View.GONE);

                } else {
                    getSuggestWord(String.valueOf(s));
                    mDeleteIv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mUiLoader.setOnRetryClickListener(() -> {
            if (mSearchPresenter != null) {
                mSearchPresenter.reSearch();
                mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
            }
        });
        mTextLayout.setClickListener(text -> {
            if (mSearchPresenter != null) {
                mSearchEt.setText(text);
                mSearchEt.setSelection(text.length());
                mSearchPresenter.Search(text);
                mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
            }
        });
        mDeleteIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchEt.setText("");
            }
        });
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {

                if (mSearchPresenter != null) {
                    mSearchPresenter.loadMore();
                }
            }
        });
        mAlbumListAdapter.setonAlbumItemCallLister((position, album) -> {
            DetailPresenter presenter = DetailPresenter.getInstance();
            presenter.setTargetAlbum(album);
            Intent intent = new Intent(SearchActivity.this, DetailsActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void getSuggestWord(String s) {
        if (mSearchPresenter != null) {
            mSearchPresenter.getRecommendWord(s);
        }
    }

    private void initView() {
        mBackBtn = findViewById(R.id.back_Iv);
        mSearchEt = findViewById(R.id.search_et);
        mSearchTv = findViewById(R.id.search_tv);
        mContentfl = findViewById(R.id.content_fl);
        /*mSearchEt.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSearchEt.requestFocus();
                mInputMethodManager.showSoftInput(mSearchEt, SHOW_IMPLICIT);
            }
        }, 500);*/
        if (mUiLoader == null) {
            mUiLoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup group) {
                    return createSuccess(group);
                }
            };
            if (mUiLoader.getParent() instanceof ViewGroup) {
                ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
            }
            mContentfl.addView(mUiLoader);

        }
        mDeleteIv = findViewById(R.id.delete_iv);
        mSearchRecommendAdapter.setOnSearchItemListener(new SearchRecommendAdapter.onSearchItemListener() {
            @Override
            public void onSearchItem(String data) {
                if (mSearchPresenter != null) {
                    mSearchPresenter.Search(data);
                }
            }
        });

    }

    private View createSuccess(ViewGroup group) {
        View inflate = LayoutInflater.from(this).inflate(R.layout.success_search, group, false);
        mSearchlist = inflate.findViewById(R.id.search_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mSearchlist.setLayoutManager(linearLayoutManager);
        mAlbumListAdapter = new AlbumListAdapter();
        mSearchlist.setAdapter(mAlbumListAdapter);
        mTextLayout = inflate.findViewById(R.id.flow_text_layout);
        mRecommendRv = inflate.findViewById(R.id.search_recommend_list);
        mRecommendRv.setLayoutManager(new LinearLayoutManager(this));
        mSearchRecommendAdapter = new SearchRecommendAdapter();
        mRecommendRv.setAdapter(mSearchRecommendAdapter);
        mRefreshLayout = inflate.findViewById(R.id.search_more);
        mRefreshLayout.setEnableRefresh(false);
        return inflate;
    }

    @Override
    public void onSearchResult(List<Album> albums) {
        handleSearchResult(albums);

    }

    private void handleSearchResult(List<Album> albums) {
        mTextLayout.setVisibility(View.GONE);
        mRefreshLayout.setVisibility(View.VISIBLE);
        mRecommendRv.setVisibility(View.GONE);
        //mInputMethodManager.hideSoftInputFromWindow(mSearchEt.getWindowToken(), HIDE_IMPLICIT_ONLY);
        if (albums != null) {
            if (albums.size() == 0) {
                if (mUiLoader != null)
                    mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
            } else {
                if (mAlbumListAdapter != null)
                    mAlbumListAdapter.setData(albums);
                if (mUiLoader != null)
                    mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
            }
        }
    }

    @Override
    public void onHotWordResult(List<HotWord> hotWordList) {
        if (hotWordList != null && hotWordList.size() > 0) {
            LogUtil.e(TAB, hotWordList.size() + "");
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
                mTextLayout.setVisibility(View.VISIBLE);
                mRefreshLayout.setVisibility(View.GONE);
                mRecommendRv.setVisibility(View.GONE);
                List<String> hotwords = new ArrayList<>();
                hotwords.clear();
                for (HotWord hotWord : hotWordList) {

                    hotwords.add(hotWord.getSearchword());
                }
                mTextLayout.setTextContents(hotwords);
            }
        }


    }

    @Override
    public void onLoadMoreResult(List<Album> result, boolean isOk) {
        if (mRefreshLayout != null) {
            mRefreshLayout.finishLoadmore();
        }
        if (isOk) {
            handleSearchResult(result);
        } else {
            ToastUtils.showToast(this, "没有更多内容");
        }
    }

    @Override
    public void onRecommendWordLoaded(List<QueryResult> results) {
        if (mSearchRecommendAdapter != null && results != null) {
            mSearchRecommendAdapter.setData(results);
        }
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
        mTextLayout.setVisibility(View.GONE);
        mRefreshLayout.setVisibility(View.GONE);
        mRecommendRv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onError(int errCode, String errMsg) {
        if (mUiLoader != null) {

                mUiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSearchPresenter != null) {
            mSearchPresenter.unregisterViewCallback(this);
        }
    }
}