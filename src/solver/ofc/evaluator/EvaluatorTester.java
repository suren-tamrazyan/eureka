package solver.ofc.evaluator;

public class EvaluatorTester
{
    public static void evalHand(String cs) {
        Evaluator ev = new Evaluator();
        long[] c = ev.encodeHand(cs);
        if(c.length == 5) {
            System.out.println(cs + " = " + ev.evalFive(c));
        }
        else {
            System.out.println(cs + " = " + ev.evalThree(c, true) + " or " + ev.evalThree(c, false));
        }
    }
    
    public static void test2() {
        evalHand("Ah2c3d4c5d");
        evalHand("Ah2h3h4h5h");
        evalHand("6h2c3d4c5d");
        evalHand("AhKcJdQcTd");
        evalHand("AhAc4d2c3d");
        evalHand("KhKc4d2c3d");
        evalHand("QhQc4d2c3d");
        evalHand("JhJc4d2c3d");
        evalHand("ThTc4d2c3d");
        evalHand("9h9c4d2c3d");
        evalHand("8h8c4d2c3d");
        evalHand("7h7c4d2c3d");
        evalHand("6h6c4d2c3d");
        evalHand("5h5c4d2c3d");
        evalHand("4h4c5d2c3d");
        evalHand("3h3c5d4c2d");
        evalHand("2h2c5d4c3d");
        evalHand("2h7c5d4c3d");
    }
    
    public static void test3() {
        evalHand("4h3h2d");
        evalHand("5h3h2d");
        evalHand("5h4h2d");
        evalHand("5h4h3d");
        evalHand("6h3h2d");
        evalHand("6h4d2d");
        evalHand("6h4d3s");
        evalHand("6h5d2s");
        evalHand("6h5d3s");
        evalHand("6h5d4s");
        evalHand("AhKhQd");
        evalHand("AsKsQs");
        evalHand("AsKs2s");
        evalHand("AsQsJs");
        evalHand("AsAhJs");
        evalHand("AsAhTs");
        evalHand("2s2h3s");
        evalHand("2s2h4s");
        evalHand("2h2c5d4c3d");
        evalHand("2s2h5s");
        evalHand("AsAhAd");
        evalHand("AsAh2s");
        evalHand("AsAh3s");
        evalHand("KsKdQs");
        evalHand("2h2s3d");
        evalHand("KsQdJhTh6s");
        
    }
    
    public static void test4() {
        evalHand("QhQs2s");
        evalHand("QhQsAs");
        evalHand("KhKs2s");
        evalHand("As2s3s");
        evalHand("As2s4s");
        evalHand("As2s5s");
        evalHand("As2s6s");
        evalHand("As3s5h");
        evalHand("5s3sAh");
        evalHand("7s2hAh");
        evalHand("7s3hAh");
        evalHand("As2s3s4s6h");
        evalHand("As2s3s4s5h");
        evalHand("2s3s4s5h7h");
        evalHand("6s2s3s4s5s");
        evalHand("AsKsQsJsTs");
    }
    
    public static void test() {
        
        long[] deck = new long[52];
        long[] hand = new long[5];
        int[] freq = new int[9];
        int a, b, c, d, e, i, j;
    
        
        for(i = 0; i < 52; i++) {
            deck[i] = Evaluator.encodeCard(i / 13, i % 13);
            
        }
    
        Evaluator ev = new Evaluator();
        // zero out the frequency array
        for ( i = 0; i < 9; i++ )
            freq[i] = 0;
    
        // loop over every possible five-card hand
        for(a=0;a<48;a++)
        {
        hand[0] = deck[a];
        for(b=a+1;b<49;b++)
        {
            hand[1] = deck[b];
            for(c=b+1;c<50;c++)
            {
            hand[2] = deck[c];
            for(d=c+1;d<51;d++)
            {
                hand[3] = deck[d];
                for(e=d+1;e<52  ;e++)
                {
                hand[4] = deck[e];
                
                
                j = Evaluator.getHandRank(ev.evalFive( hand )).ordinal();
                freq[j]++;
                }
            }
            }
        }
        }
    
        for(i=0;i<9;i++)
        System.out.println(freq[i] );
    }

    public static void main(String[] args) {
        test3();
    }
}
