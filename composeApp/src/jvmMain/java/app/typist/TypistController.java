package app.typist;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import condition.Condition;
import mvc.Controller;

/**
 * A class that serves as the Controller (C) in MVC. Specializes in handling control flow.
 */
public class TypistController extends Controller
{

    /**
     * Constructor for creating the controller.
     */
    public TypistController()
    {
        super();
        return;
    }

    /**
     * Registers listeners to detect events.
     */
    public void initializeEvents()
    {
        TypistView aView = this.getView();

        aView.submitButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                handleInput();
            }
        });

        aView.fileSelectionButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                handleImageSelection();
            }
        });

        aView.retryButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                handleRetry();
            }
        });

        aView.closeButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                handleClose();
            }
        });
    }

    /**
     * Returns its own model.
     *
     * @return The model for this controller.
     */
    public TypistModel getModel()
    {
        return (TypistModel)(this.model);
    }

    /**
     * Returns its own view.
     *
     * @return The view for this controller.
     */
    public TypistView getView()
    {
        return (TypistView)(this.view);
    }

    /**
     * Passes input data received from the View to the Model.
     */
    private void handleInput()
    {
        TypistModel aModel = this.getModel();
        TypistView aView = this.getView();
        String aString = aView.inputText();
        System.out.printf("[controller] input string: %s%n", aView.inputText());

        Boolean hasValidTypingLength = aModel.hasValidTypingLength(aString);
        Boolean hasImageFile = aModel.hasImageFile(aView.imageFile());

        new Condition(() -> hasValidTypingLength).ifThenElse(() -> { new Condition(() -> !hasImageFile).ifThenElse(() -> { JOptionPane.showMessageDialog(null, "画像ファイルが選択されていません。", "画像未選択", JOptionPane.WARNING_MESSAGE); }, () -> {
                // Generate the typist art and start the animation.
                aView.inputImage();
                aModel.perform(); }); }, () -> {
            JOptionPane.showMessageDialog(null, "もう一度入力してください。\n文字数の範囲は32〜128です。", "入力エラー", JOptionPane.WARNING_MESSAGE);

            // Clear the text box.
            aView.inputText(""); });

        return;
    }

    /**
     * Displays the file selection screen and passes the selected image data to the View.
     */
    private void handleImageSelection()
    {
        TypistView aView = this.getView();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("タイピストアートに変換する画像を選択してください");

        // Set a file filter to allow only image files to be selected.
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "画像ファイル", "jpg", "jpeg", "png");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(null);
        new Condition(() -> result == JFileChooser.APPROVE_OPTION).ifTrue(() -> {
            File aFile = fileChooser.getSelectedFile();
            System.out.println("[controller] selected image: " + aFile.getAbsolutePath());

            aView.imageFile(aFile);
            aView.updateFileNameLabel();
        });

        return;
    }

    /**
     * Clears the model's data and reopens the input screen.
     */
    private void handleRetry()
    {
        TypistModel aModel = this.getModel();
        TypistView aView = this.getView();

        aView.outputFrame().dispose();
        aView.retryFrame().dispose();

        aModel.flush();
        aView.inputText(null);
        aView.imageFile(null);
        aView.inputScreen();

        return;
    }

    /**
     * Closes all windows and terminates the application.
     */
    private void handleClose()
    {
        System.exit(0);
    }

    /**
     * Called when a mouse button is pressed.
     *
     * @param aMouseEvent The mouse event.
     */
    public void mousePressed(MouseEvent aMouseEvent)
    {
        Integer x = aMouseEvent.getX();
        Integer y = aMouseEvent.getY();
        System.out.printf("座標(%d,%d)\n", x, y);

        return;
    }

    /**
     * Called when a mouse button is released.
     *
     * @param aMouseEvent The mouse event.
     */
    public void mouseReleased(MouseEvent aMouseEvent)
    {
        System.out.println("マウスボタンが離れました");

        return;
    }
}
