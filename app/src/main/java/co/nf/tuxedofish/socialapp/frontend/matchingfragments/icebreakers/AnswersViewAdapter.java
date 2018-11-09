package co.nf.tuxedofish.socialapp.frontend.matchingfragments.icebreakers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import co.nf.tuxedofish.socialapp.R;

import java.util.ArrayList;

public class AnswersViewAdapter extends RecyclerView.Adapter<AnswersViewAdapter.MyViewHolder>{
    private Context mContext;
    private String[] answersList;

    private ArrayList<Boolean> selected;

    private RadioButton lastChecked = null;
    private int lastCheckedPos = 0;

    private boolean selectMultiple = false;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public RadioButton answer;
        private View view;

        public MyViewHolder(View view) {
            super(view);
            answer = (RadioButton) view.findViewById(R.id.answerButton);
            answer.setEnabled(false);
            this.view = view;
            selected = new ArrayList<Boolean>();
            for(int i=0; i<answersList.length; i++) {selected.add(false); }
        }
    }

    public AnswersViewAdapter(Context mContext) {
        this.mContext = mContext;
        this.answersList = new String[]{};
    }

    public void setData(String[] answers) {
        this.answersList = answers;
    }

    public void setIfSelectMultiple(boolean selectMultiple) {
        this.selectMultiple = selectMultiple;
    }

    public ArrayList<Boolean> getSelected() {
        return selected;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.answer, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        String answer = answersList[position];
        holder.answer.setText(answer);
        holder.answer.setTag(new Integer(position));

        //See below
        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                RadioButton cb = (RadioButton) v.findViewById(R.id.answerButton);
                int clickedPos = (Integer)cb.getTag();

                if(!selectMultiple) {
                    if (cb.isChecked()) {
                        cb.setChecked(false);
                        selected.set(clickedPos, false);

                        lastChecked = null;
                        lastCheckedPos = clickedPos;
                    } else {
                        cb.setChecked(true);
                        selected.set(clickedPos, true);

                        Log.d("info", Boolean.toString(lastChecked != null));
                        if (lastChecked != null) {
                            lastChecked.setChecked(false);
                            selected.set(lastCheckedPos, false);
                        }

                        lastChecked = cb;
                    }
                } else {
                    cb.setChecked(!cb.isChecked());
                    selected.set(clickedPos, cb.isChecked());
                }
            }
        };

        //Same behaviour if we click the box as if we click the cardview
        holder.view.setOnClickListener(onClickListener);
        holder.answer.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        return answersList.length;
    }
}
