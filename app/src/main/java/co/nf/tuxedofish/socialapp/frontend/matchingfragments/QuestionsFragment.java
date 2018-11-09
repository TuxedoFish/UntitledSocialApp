package co.nf.tuxedofish.socialapp.frontend.matchingfragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.nf.tuxedofish.socialapp.R;
import co.nf.tuxedofish.socialapp.frontend.matchingfragments.icebreakers.AnswersViewAdapter;
import co.nf.tuxedofish.socialapp.frontend.matchingfragments.icebreakers.QuestionAnswerPair;

public class QuestionsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private AnswersViewAdapter adapter;
    private List<QuestionAnswerPair> answersQuestionList;
    private int current_question;

    private TextView question;
    private TextView questionCount;
    private Button nextButton;

    private OnFragmentInteractionListener mListener;

    public QuestionsFragment() {

    }

    public static QuestionsFragment newInstance(String param1, String param2) {
        QuestionsFragment fragment = new QuestionsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_questions, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void finishedQuestions();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.answersView);

        answersQuestionList = new ArrayList<>();

        adapter = new AnswersViewAdapter(getContext());

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);

        question = view.findViewById(R.id.questionToBeAsked);
        questionCount = view.findViewById(R.id.questionCount);

        addQuestion("Are you a fan of Thor Ragnorok", new String[] {"yes", "no"}, false);
        addQuestion("What TV shows do you like", new String[] {"Big Bang Theory", "Simpsons", "Futurama"}, true);

        current_question=0; updateAdapter(current_question);

        nextButton = view.findViewById(R.id.next_question);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextQuestion();
            }
        });
    }

    public void nextQuestion() {
        current_question ++; updateAdapter(current_question);
    }

    public void addQuestion(String question, String[] answers, boolean selectMultiple) {
        answersQuestionList.add(new QuestionAnswerPair(question, answers, selectMultiple));
    }

    public void updateAdapter(int current_question) {
        if(current_question>=answersQuestionList.size()) {
            //Here we have no more questions to ask so go to the matching screen
            mListener.finishedQuestions();
        } else{
            //We still have questions to ask so reset data;
            adapter.setData(answersQuestionList.get(current_question).getAnswers());
            adapter.setIfSelectMultiple(answersQuestionList.get(current_question).canSelectMultiple());
            adapter.notifyDataSetChanged();
            //Check if we are on the last question
            if(current_question+1 == answersQuestionList.size()) { nextButton.setText(R.string.questions_last); }
            questionCount.setText(Integer.toString(current_question+1) + "/" + Integer.toString(answersQuestionList.size()));
            question.setText(answersQuestionList.get(current_question).getQuestion());
        }
    }
}
