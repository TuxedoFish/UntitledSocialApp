package co.nf.tuxedofish.socialapp.frontend.matchingfragments.icebreakers;

import java.util.ArrayList;

public class QuestionAnswerPair {
    private String question;
    private String[] answers;
    private ArrayList<Boolean> selected;
    private boolean selectMultiple;

    public QuestionAnswerPair(String question, String[] answers, boolean selectMultiple) {
        //Initialise new variables
        this.question = question; this.answers = answers; this.selectMultiple = selectMultiple;
        //Set up false across the array
        this.selected = new ArrayList<Boolean>();
        for(int i=0; i<answers.length; i++) {this.selected.add(false);}
    }

    public Boolean canSelectMultiple() { return selectMultiple; }
    public String getQuestion() { return question; }
    public String getAnswer(int index) { return answers[index]; }
    public String[] getAnswers() { return answers; }
    public void setSelected(boolean isSelected, int index) { this.selected.set(index, isSelected); }
}
