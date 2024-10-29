import org.json.*;
import java.awt.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.awt.image.BufferedImage;

class GlobalVariables {
    public static String apiKey = "YOUR API KEY HERE";
}

class MenuFetcher {
    public String fetchRecipesByIngredients(String ingredients, String apiKey) throws IOException {
        String urlString = "https://api.spoonacular.com/recipes/findByIngredients?ingredients=" + ingredients + "&apiKey=" + apiKey;

        URL url = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        } else {
            System.out.println("GET request not successful.");
            return null;
        }
    }

    public String fetchRecipesById(int id, String apiKey) throws IOException {
        String urlString = "https://api.spoonacular.com/recipes/" + id +"/information?apiKey=" + apiKey;

        URL url = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        } else {
            System.out.println("GET request not successful.");
            return null;
        }
    }
}




class MenuPanel extends JPanel {
    private final JTextField searchBox;
    private final JPanel scrollContent;
    private final CardLayout cardLayout;
    private final JPanel parentPanel;

    MenuPanel(CardLayout cardLayout, JPanel parentPanel) {
        this.cardLayout = cardLayout;
        this.parentPanel = parentPanel;

        setLayout(null);
        setBackground(new Color(30, 33, 36));

        searchBox = new JTextField("Search for recipes (comma separated)");
        searchBox.setBounds(14, 165, 590, 43);
        searchBox.setForeground(Color.GRAY);
        searchBox.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchBox.getText().equals("Search for recipes (comma separated)")) {
                    searchBox.setText("");
                    searchBox.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchBox.getText().isEmpty()) {
                    searchBox.setForeground(Color.GRAY);
                    searchBox.setText("Search for recipes (comma separated)");
                }
            }
        });
        add(searchBox);

        JButton searchButton = new JButton("Search");
        searchButton.setBounds(612, 165, 104, 43);
        searchButton.setForeground(Color.WHITE);
        searchButton.setBackground(new Color(119, 118, 179));
        add(searchButton);

        try {
            URL logoUrl = new URL("https://i.imgur.com/lLe2jLi.png");
            BufferedImage logoImage = ImageIO.read(logoUrl);

            Image scaledLogo = logoImage.getScaledInstance(330, 89, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
            logoLabel.setBounds(200, 42, 330, 89);
            add(logoLabel);
        } catch (Exception e) {
            System.out.println(e);
        }


        scrollContent = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        scrollContent.setBackground(new Color(54, 57, 62));

        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setBounds(14, 235, 705, 269);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(scrollPane);

        MenuFetcher menuFetcher = new MenuFetcher();

        searchButton.addActionListener(e -> {
            String items = searchBox.getText();
            if (!items.isEmpty()) {
                items = items.replaceAll(",", ",+");
                scrollContent.removeAll();

                try {
                    String responseJson = menuFetcher.fetchRecipesByIngredients(items, GlobalVariables.apiKey);
                    JSONArray jsonArray = new JSONArray(responseJson);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject recipe = jsonArray.getJSONObject(i);

                        int id = recipe.getInt("id");
                        String title = recipe.getString("title");
                        String image = recipe.getString("image");

                        JSONArray missedIngredients = recipe.getJSONArray("missedIngredients");
                        StringBuilder missingIngs = new StringBuilder();

                        for (int j = 0; j < missedIngredients.length(); j++) {
                            JSONObject ingredient = missedIngredients.getJSONObject(j);
                            String ingredientName = ingredient.getString("name");
                            if (j == missedIngredients.length()-1) {missingIngs.append(ingredientName).append(".");} else {missingIngs.append(ingredientName).append(", ");}
                        }
                        addRecipeCard(title, image, id, missingIngs.toString());
                    }

                    scrollContent.revalidate();
                    scrollContent.repaint();

                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    public void addRecipeCard(String name, String imagePath, int id, String missing) {
        JPanel recipeCard = new JPanel();
        recipeCard.setPreferredSize(new Dimension(218, 195));
        recipeCard.setBackground(new Color(66, 69, 73));
        recipeCard.setLayout(null);

        recipeCard.putClientProperty("recipeId", id);
        recipeCard.putClientProperty("missingIngredients", missing);

        try {
            URL imageUrl = new URL(imagePath);

            BufferedImage foodImage = ImageIO.read(imageUrl);
            Image scaledFoodImage = foodImage.getScaledInstance(218, 155, Image.SCALE_SMOOTH);

            JLabel foodImageLabel = new JLabel(new ImageIcon(scaledFoodImage));
            foodImageLabel.setBounds(0, 0, 218, 155);
            recipeCard.add(foodImageLabel);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JLabel itemName = new JLabel(name);
        itemName.setBounds(12, 160, 193, 27);
        itemName.setForeground(Color.WHITE);
        itemName.setFont(new Font("SansSerif", Font.PLAIN, 19));
        recipeCard.add(itemName);

        recipeCard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ((DetailPanel) ((JPanel) parentPanel).getComponent(1)).setRecipeDetails(name, imagePath, missing, id);
                cardLayout.show(parentPanel, "detailPanel");
            }
        });

        scrollContent.add(recipeCard);
        scrollContent.revalidate();
        scrollContent.repaint();
    }
}

class DetailPanel extends JPanel {
    private final JLabel recipeNameLabel;
    private final JLabel recipeImageLabel;
    private final JLabel missingIngredientsText;
    private final JTextPane recipeTextPane;

    DetailPanel(CardLayout cardLayout, JPanel parentPanel) {
        setLayout(new BorderLayout());
        setBackground(new Color(66, 69, 73));

        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(30,33,36));
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton backButton = new JButton("Back");
        backButton.setBackground(new Color(114, 137, 218));
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(e -> cardLayout.show(parentPanel, "menuPanel"));
        topPanel.add(backButton);

        recipeNameLabel = new JLabel("Food Item Name");
        recipeNameLabel.setForeground(Color.WHITE);
        recipeNameLabel.setFont(new Font("Arial Black", Font.BOLD, 20));
        topPanel.add(recipeNameLabel);

        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new Color	(40,43,48));
        centerPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        recipeImageLabel = new JLabel();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        centerPanel.add(recipeImageLabel, gbc);

        JLabel missingIngredientsLabel = new JLabel("Missing Ingredients:");
        missingIngredientsLabel.setForeground(Color.WHITE);
        missingIngredientsLabel.setFont(new Font("Arial Bold", Font.BOLD, 13));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        centerPanel.add(missingIngredientsLabel, gbc);

        missingIngredientsText = new JLabel();
        missingIngredientsText.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        centerPanel.add(missingIngredientsText, gbc);

        add(centerPanel, BorderLayout.WEST);

        recipeTextPane = new JTextPane();
        recipeTextPane.setEditable(false);
        recipeTextPane.setForeground(Color.WHITE);
        recipeTextPane.setBackground(new Color(54, 57, 62));
        recipeTextPane.setBorder(null);

        JScrollPane recipeScrollPane = new JScrollPane(recipeTextPane);
        recipeScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        recipeScrollPane.setPreferredSize(new Dimension(300, 400));
        recipeScrollPane.setBorder(null);
        add(recipeScrollPane, BorderLayout.CENTER);
    }

    public void setRecipeDetails(String name, String imagePath, String missing, int id) {
        recipeNameLabel.setText(name);
        missingIngredientsText.setText(missing);
        try {
            URL url = new URL(imagePath);
            BufferedImage recipeImage = ImageIO.read(url);
            Image scaledRecipeImage = recipeImage.getScaledInstance(200, 150, Image.SCALE_SMOOTH);
            recipeImageLabel.setIcon(new ImageIcon(scaledRecipeImage));
        } catch (IOException e) {
            e.printStackTrace();
        }

        MenuFetcher menuFetcher = new MenuFetcher();

        try {
            String responseJson = menuFetcher.fetchRecipesById(id, GlobalVariables.apiKey);

            JSONObject jsonObject = new JSONObject(responseJson);
            JSONArray ingredientsArray = jsonObject.getJSONArray("extendedIngredients");

            StringBuilder recipeList = new StringBuilder();

            for (int i = 0; i < ingredientsArray.length(); i++) {
                JSONObject ingredient = ingredientsArray.getJSONObject(i);
                String original = ingredient.getString("original");
                recipeList.append("- ").append(original).append("\n");
            }

            recipeTextPane.setText(recipeList.toString());

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}

public class BiteBuddy {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("BiteBuddy");
            frame.setSize(750, 569);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            CardLayout cardLayout = new CardLayout();
            JPanel parentPanel = new JPanel(cardLayout);

            MenuPanel menuPanel = new MenuPanel(cardLayout, parentPanel);
            parentPanel.add(menuPanel, "menuPanel");

            DetailPanel detailPanel = new DetailPanel(cardLayout, parentPanel);
            parentPanel.add(detailPanel, "detailPanel");

            frame.add(parentPanel);
            frame.setVisible(true);
        });
    }
}