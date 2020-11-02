public class CreateThreads {

    public CreateThreads() {
        View view = new View();

        /*Here, we initialize the Manager, Box, and Cat threads. Initially we have 5 boxes and 12 cats.
        * We pass the view to all the classes in order to update the "List of Actions" console with any important action that happens.*/

        try {
            BoxManager bm1 = new BoxManager(8888, view);
            Thread bmThread = new Thread(bm1);
            bmThread.start();

            Box box1 = new Box(25, 4444, view);
            Thread box1Thread = new Thread(box1);
            box1Thread.start();

            Box box2 = new Box(30, 4445, view);
            Thread box2Thread = new Thread(box2);
            box2Thread.start();

            Box box3 = new Box(35, 4446, view);
            Thread box3Thread = new Thread(box3);
            box3Thread.start();

            Box box4 = new Box(32, 4447, view);
            Thread box4Thread = new Thread(box4);
            box4Thread.start();

            Box box5 = new Box(29, 4448, view);
            Thread box5Thread = new Thread(box5);
            box5Thread.start();


            Cat cat1b1 = new Cat(9, 4444, view);
            Thread catThread1b1 = new Thread(cat1b1);
            catThread1b1.start();

            Cat cat2b1 = new Cat(9, 4444, view);
            Thread catThread2b1 = new Thread(cat2b1);
            catThread2b1.start();

            Cat cat3b1 = new Cat(9, 4444, view);
            Thread catThread3b1 = new Thread(cat3b1);
            catThread3b1.start();


            Cat cat1b2 = new Cat(9, 4445, view);
            Thread catThread1b2 = new Thread(cat1b2);
            catThread1b2.start();

            Cat cat2b2 = new Cat(9, 4445, view);
            Thread catThread2b2 = new Thread(cat2b2);
            catThread2b2.start();


            Cat cat1b3 = new Cat(9, 4446, view);
            Thread catThread1b3 = new Thread(cat1b3);
            catThread1b3.start();

            Cat cat2b3 = new Cat(9, 4446, view);
            Thread catThread2b3 = new Thread(cat2b3);
            catThread2b3.start();


            Cat cat1b4 = new Cat(9, 4447, view);
            Thread catThread1b4 = new Thread(cat1b4);
            catThread1b4.start();

            Cat cat2b4 = new Cat(9, 4447, view);
            Thread catThread2b4 = new Thread(cat2b4);
            catThread2b4.start();

            Cat cat3b4 = new Cat(9, 4447, view);
            Thread catThread3b4 = new Thread(cat3b4);
            catThread3b4.start();


            Cat cat1b5 = new Cat(9, 4448, view);
            Thread catThread1b5 = new Thread(cat1b5);
            catThread1b5.start();

            Cat cat2b5 = new Cat(9, 4448, view);
            Thread catThread2b5 = new Thread(cat2b5);
            catThread2b5.start();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
