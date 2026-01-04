package ca.lizardwizard.ebookclient;

import ca.lizardwizard.ebookclient.Lib.ApiCalls;
import ca.lizardwizard.ebookclient.Lib.AudioPlayer;
import ca.lizardwizard.ebookclient.objects.Book;


public class TestMain {
    public static void main(String[] args) {
        try {
            int id = 1;
            // 1) Pull book data
            Book book = ApiCalls.getBook(id);
            // 2) Download first chunk and load into in-memory player
            AudioPlayer player = new AudioPlayer(book);
            player.loadFromBytes(0);
            System.out.println(player.grabClipLength(0));
            System.out.println(player.grabClipLength(1));
            System.out.println(player.grabClipLength(2));
            System.out.println(player.grabClipLength(3));
            player.play();

            while (true){

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
