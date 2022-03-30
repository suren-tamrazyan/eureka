/*
 * Copyright (C) 2013 Ilkka Kokkarinen

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
 */

/* A simple Swing GUI component to use the OFC evaluator. */
package solver.ofc.evaluator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;

public class EvaluatorGUI extends JPanel {

    private boolean busy = false;
    private DecimalFormat fm = new DecimalFormat();
    private Font font = new Font("Times", Font.PLAIN, 18);
    private Thread thread;
    private JTextArea text = new JTextArea();
    
    private JPanel createPanel(String title, JTextField text) {
        JPanel result = new JPanel();
        result.setLayout(new FlowLayout());
        result.setPreferredSize(new Dimension(250,30));
        result.add(text);
        result.add(new JLabel(title));
        return result;
    }

    private void message(String msg) {
        text.append(msg);
        text.setCaretPosition(text.getDocument().getLength());
    }
    
    public EvaluatorGUI() {
        fm.setMaximumFractionDigits(3);
        fm.setMinimumFractionDigits(3);

        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.setPreferredSize(new Dimension(400,650));

        String[] titles = {"Hero Front", "Hero Middle", "Hero Back",
                "Villain Front", "Villain Middle", "Villain Back",
                "Hero Card", "Time Limit (sec)", "Threads", "Fantasyland Value" };
        String[] initValues = {"QdQc", "9hTs9s", "6h6c", "3c6d", "Jh9c4s", "QsQh7d", "4h", "3", "5", "15" };

        final JTextField[] hands = new JTextField[titles.length];
        for(int i = 0; i < titles.length; i++) {
            hands[i] = new JTextField((i == 0 || i == 3) ? 6: ((i > 5)? 2: 10));
            hands[i].setText(initValues[i]);
            hands[i].setFont(font);
            this.add(createPanel(titles[i], hands[i]));
        }

        final JButton bu1 = new JButton("One Card"); 
        final JButton bu2 = new JButton("All Cards");
        JButton bu3 = new JButton("Clear");       
        bu3.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    for(int i = 0; i < 7; i++) {
                        hands[i].setText("");
                    }
                }
            });

        JButton bu4 = new JButton("Swap");
        bu4.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    String tmp = hands[0].getText();
                    hands[0].setText(hands[3].getText());
                    hands[3].setText(tmp);

                    tmp = hands[1].getText();
                    hands[1].setText(hands[4].getText());
                    hands[4].setText(tmp);

                    tmp = hands[2].getText();
                    hands[2].setText(hands[5].getText());
                    hands[5].setText(tmp);
                }

            });

        JPanel buttons = new JPanel();
        buttons.setPreferredSize(new Dimension(300, 30));
        buttons.add(bu1);
        buttons.add(bu2);
        buttons.add(bu3);
        buttons.add(bu4);
        this.add(buttons);

        message("Open Face Chinese Poker Evaluator 0.18\n");
        message("Ilkka Kokkarinen, 10 September 2013\n");
        message("This program comes with ABSOLUTELY NO WARRANTY\n");
        message("This is free software, and you are welcome to\n");
        message("modify and distribute it under certain conditions.\n");
        final JScrollPane sp = new JScrollPane(text);
        sp.setPreferredSize(new Dimension(370,300));
        this.add(sp);

        class MyActionListener implements ActionListener {
            private boolean allCards;
            public MyActionListener(boolean allCards) { this.allCards = allCards; }

            public void actionPerformed(ActionEvent ae) {
                if(busy) { return; }
                Board.sampleCount = 0;               
                final Deck d = new Deck();
                for(int i = 0; i < 7; i++) {
                    hands[i].setText(hands[i].getText().replaceAll(" ", ""));
                }
                Evaluator ev = new Evaluator();
                final Board b1 = new Board(hands[0].getText(), hands[1].getText(), hands[2].getText(), d, ev);
                final Board b2 = new Board(hands[3].getText(), hands[4].getText(), hands[5].getText(), d, ev);
                if(!(b1.getCount() == b2.getCount() || b1.getCount() == b2.getCount() - 1)) {
                    message("Error: invalid card counts\n");
                    return;
                }
                final long card = Evaluator.encodeCard(hands[6].getText());
               
                busy = true;
                bu1.setEnabled(false);
                bu2.setEnabled(false);
                long tl;
                try {
                    tl = Integer.parseInt(hands[7].getText()) * 1000;
                } catch(NumberFormatException e) {
                    tl = 5000;
                }
                final long timeLimit = tl;
                int t;
                try {
                    t = Integer.parseInt(hands[8].getText());
                }
                catch(NumberFormatException e) {
                    t = 1;
                }
                final int threads = t;
                try {
                    Board.fantasyLand = Integer.parseInt(hands[9].getText());
                }
                catch(NumberFormatException e) {
                    Board.fantasyLand = 0;
                }
                if(!allCards) {
                    d.removeCard(card);
                    thread = new Thread(new Runnable() {
                            public void run() {
                                message("-------------------\n");
                                message("Hero: " + b1 + "\n");
                                message("Villain: " + b2 + "\n");
                                message("Placing card " + Evaluator.decodeCard(card) + "...");
                                double[] v;
                                try {
                                    v = b1.maxValueThreads(card, b2, d, timeLimit, threads, true);
                                } catch(Exception e) { v = new double[3]; }
                                message(" finished " + Board.sampleCount + " samples\n");
                                if(b1.idx[0] < 3) {
                                    message("Front:\t" + fm.format(v[0]) + "\n");
                                }
                                if(b1.idx[1] < 5) {
                                    message("Middle:\t" + fm.format(v[1]) + "\n");
                                }
                                if(b1.idx[2] < 5) {
                                    message("Back:\t" + fm.format(v[2]) + "\n");
                                }
                                busy = false;
                                bu1.setEnabled(true);
                                bu2.setEnabled(true);
                            }
                        });
                }
                else {
                    thread = new Thread(new Runnable() {
                            public void run() {
                                message("-------------------\n");
                                message("Hero: " + b1 + "\n");
                                message("Villain: " + b2 + "\n");
                                double total = 0.0;
                                for(int i = 0; i < d.getSize(); i++) {
                                    long card = d.cards[i];
                                    Deck d2 = new Deck(d);
                                    d2.removeCard(card);
                                    message(Evaluator.decodeCard(card) + "... ");
                                    double[] v;
                                    try {
                                        v = new Board(b1).maxValueThreads(card, new Board(b2), d2, timeLimit, threads, true);
                                    } catch(Exception e) { v = new double[3]; }
                                    double best = -1000;
                                    String bestMove = "";
                                    if(b1.idx[0] < 3 && v[0] > best) { best = v[0]; bestMove = "front"; }
                                    if(b1.idx[1] < 5 && v[1] > best) { best = v[1]; bestMove = "middle"; }
                                    if(b1.idx[2] < 5 && v[2] > best) { best = v[2]; bestMove = "back"; }
                                    message(fm.format(best) + " (" + bestMove + ")\n");
                                    total += best;
                                }
                                message("Finished " + Board.sampleCount + " samples\n");
                                message("Average result over all cards: " + fm.format(total / d.getSize()) + "\n");
                                bu1.setEnabled(true);
                                bu2.setEnabled(true);
                                busy = false; 
                            }
                        });
                }
                thread.start();
            }          
        }

        bu1.addActionListener(new MyActionListener(false)); // One Card
        bu2.addActionListener(new MyActionListener(true)); //  All Cards

    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Open Face Chinese Evaluator");
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLayout(new FlowLayout());
        f.add(new EvaluatorGUI());
        f.pack();
        f.setVisible(true);        
    }
}