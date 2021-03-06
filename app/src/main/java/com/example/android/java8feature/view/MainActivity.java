package com.example.android.java8feature.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.java8feature.R;
import com.example.android.java8feature.ToDoAdapter;
import com.example.android.java8feature.model.ToDoModel;
import com.example.android.java8feature.presenter.TodoContract;
import com.example.android.java8feature.presenter.TodoPresenter;
import com.example.android.java8feature.utils.DividerItemDecoration;
import com.example.android.java8feature.utils.FilterUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements TodoContract.View, ToDoAdapter.ItemClickListener, SearchView.OnQueryTextListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ProgressBar progressBar;
    private TextView emptyView;
    private RecyclerView rcyvTodo;
    private ToDoAdapter mAdapter;
    private List<ToDoModel> mToDos;
    private ImageView imgSort;
    private TodoContract.UserActionListener mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        mPresenter = new TodoPresenter(this);

        // fetch ToDos list
        mPresenter.loadTodos();
    }

    private void initViews() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        emptyView = (TextView) findViewById(R.id.emptyView);
        rcyvTodo = (RecyclerView) findViewById(R.id.rcyvTodo);
        imgSort = (ImageView) findViewById(R.id.imgSort);

        mToDos = new ArrayList<>();

        rcyvTodo.setHasFixedSize(true);
        rcyvTodo.setLayoutManager(new LinearLayoutManager(this));
        rcyvTodo.addItemDecoration(new DividerItemDecoration(this));
        mAdapter = new ToDoAdapter(this);
        rcyvTodo.setAdapter(mAdapter);

        /**
         * Simple Lambda expression use case for {@link View.android.view.View.OnClickListener}
         *
         */
        imgSort.setOnClickListener(v ->
                /** Lambda expression for {@link java.util.Comparator}*/
                mToDos.sort((ToDoModel todo1, ToDoModel todo2) -> todo1.getTitle().compareTo(todo2.getTitle()))
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // http://cr.openjdk.java.net/~mr/se/8/java-se-8-pfd-spec/java-se-8-jls-pfd-diffs.pdf
        // http://stackoverflow.com/a/21833777
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public void setProgressBar(boolean active) {
        if (active) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void onFilterdData(List<ToDoModel> todos) {
        mAdapter.addItems(todos);
    }

    @Override
    public void onSuccessLoadTodos(List<ToDoModel> todos) {
        mToDos.clear();
        mToDos.addAll(todos);
        mAdapter.addItems(mToDos);
    }

    @Override
    public void onError() {
        emptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(int pos) {
        Toast.makeText(this, mToDos.get(pos).getTitle(), Toast.LENGTH_SHORT).show();
        /** These are the methods {@link FilterUtils#printTodosWithPredicate} */
        FilterUtils.printTodosWithPredicate(mToDos, m -> m.getTitle().length() > 20);
        FilterUtils.printTodosWithPredicateConsume(mToDos, m -> m.getTitle().length() > 20, ToDoModel::printTodo);
        FilterUtils.printTodosWithPredicateFunctionConsume(mToDos, m -> m.getTitle().length() > 20, ToDoModel::getTitle, title -> Log.e(TAG, "onItemClick: " + title));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        /**
         * Here we are using functional interface with Lambda expression also check last method argument which
         * makes use of Method Reference {@see <a href="https://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html">Method Reference</a>}
         */
        FilterUtils.filter(mToDos, m -> m.getTitle().toLowerCase(), s -> s.contains(query), this::onFilterdData);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
