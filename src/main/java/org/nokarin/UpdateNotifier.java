/**
 * All Rights Reserved
 * <p>
 * Copyright © 2025 by nokarin. All Rights Reserved.
 * <p>
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION
 * <p>
 * 1. Definitions:
 *    - "Author" refers to nokarin.
 *    - "Project" refers to the NekoUI project, including all associated source code, assets, and documentation.
 *    - "You" or "Your" refers to any individual or legal entity exercising permissions granted by this license.
 *    - "Background Assets" refers to visual background files (static or animated) used by the Project, whether bundled or provided separately via external resource packs.
 * <p>
 * 2. Grant of License:
 *    The Author grants You a personal, non-transferable, non-exclusive, revocable, limited license to use the Project strictly in accordance with the terms of this Agreement. You may use the Project for personal, non-commercial purposes only.
 * <p>
 * 3. Restrictions:
 *    You are explicitly restricted from all of the following:
 *    a. Selling, leasing, renting, licensing, sublicensing, distributing, or otherwise commercializing the Project or any derivative works thereof;
 *    b. Modifying, merging, adapting, translating, making derivative works of, or reverse engineering the Project, excluding Background Assets;
 *    c. Removing, altering, or obscuring any proprietary notices (including copyright or trademark notices) of the Author;
 *    d. Using the Project in any manner that could damage, disable, overburden, or impair the Project or interfere with any other party's use and enjoyment of the Project;
 *    e. Storing, archiving, or keeping copies of the Project, in part or in whole, beyond personal, non-commercial use as outlined above;
 *    f. Re-publishing, uploading, or making the Project available to the public in any form without following the Attribution and Sharing Guidelines (see Section 13).
 * <p>
 * 4. Non-Commercial Use:
 *    The Project is provided for non-commercial use only. You are not allowed to use the Project, or any part thereof, for any commercial purpose or for any financial gain, except as permitted in Section 13.
 * <p>
 * 5. No Affiliation Disclaimer:
 *    The Project is an independent creation and is not affiliated with, endorsed by, or associated with any third-party game, company, or brand. Any external assets (including custom backgrounds) created or used by users are the sole responsibility of their respective creators.
 * <p>
 * 6. Ownership:
 *    The Author retains all rights, title, and interest in and to the Project, including all intellectual property rights therein. This Agreement does not convey to You any rights of ownership in or related to the Project, but only a limited right of use as expressly set forth herein.
 * <p>
 * 7. Termination:
 *    This License remains in effect as long as You follow all terms stated herein. If You violate any part of this License — including but not limited to modifying or redistributing the Project without permission — Your right to use the Project will be considered terminated. You must then immediately stop using the Project and delete all copies in your possession. Continued use or public distribution of the Project after your rights have been terminated will be considered a violation of copyright law and may result in reporting or legal action. The Author reserves the right to take appropriate action if the License is violated.
 * <p>
 * 8. Disclaimer of Warranties:
 *    The Project is provided "AS IS" and "AS AVAILABLE" without warranty of any kind, either express or implied, including but not limited to the implied warranties of merchantability, fitness for a particular purpose, or non-infringement. The Author does not warrant that the Project will meet Your requirements, operate without interruption, or be free of errors.
 * <p>
 * 9. Limitation of Liability:
 *    In no event shall the Author be liable for any damages whatsoever (including, without limitation, direct, indirect, incidental, consequential, special, or exemplary damages, or damages for loss of profits, revenue, data, or use) arising out of or in connection with the Project or this Agreement, whether in an action of contract, tort (including negligence), strict liability, or otherwise, even if advised of the possibility of such damages.
 * <p>
 * 10. Governing Law:
 *    This Agreement shall be governed by and construed in accordance with the laws of Indonesia, without regard to its conflict of laws principles. Any legal action or proceeding arising under this Agreement will be brought exclusively in the federal or state courts located in Indonesia.
 * <p>
 * 11. Entire Agreement:
 *    This Agreement constitutes the entire agreement between You and the Author regarding the use of the Project and supersedes all prior and contemporaneous written or oral agreements between You and the Author.
 * <p>
 * 12. Changes to this Agreement:
 *    The Author reserves the right, at its sole discretion, to modify or replace this Agreement at any time. If a revision is material, the Author will provide at least 30 days' notice prior to any new terms taking effect. What constitutes a material change will be determined at the Author’s sole discretion.
 * <p>
 * 13. Attribution and Sharing Guidelines:
 *    a. You are allowed to use and display the Project (including in screenshots, videos, and livestreams) for content creation purposes such as YouTube videos, Twitch streams, or similar, as long as the content is not monetizing the Project directly (e.g., charging access to the mod or using the mod as the main revenue driver).
 *    b. Sharing the Project is permitted only if You share the original and official links to the Project (such as the official GitHub, Modrinth, CurseForge, or the official NekoUI website).
 *    c. You are not allowed to share the Project using monetized links (such as ad shorteners or paid download mirrors).
 *    d. Any re-upload or redistribution must include clear attribution to the Author and must not misrepresent the source or author of the Project.
 * <p>
 * By using the Project, You acknowledge that You have read this Agreement, understand it, and agree to be bound by its terms and conditions.
 */
package org.nokarin;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;

public class UpdateNotifier {
    private static final JTextArea logArea = new JTextArea(8, 40);
    private static final JScrollPane logScroll = new JScrollPane(logArea);
    private static boolean logVisible = false;

    public static void main(String[] args) throws Exception {
        log("Starting NekoUI Update Notifier...");
        String version = args.length > 0 ? args[0] : null;
        String loader = args.length > 1 ? args[1] : null;
        String mcVersion = args.length > 2 ? args[2] : null;
        String modsPath = args.length > 3 ? args[3] : null;

        try {
            // Setup FlatLaf theme
            FlatMacDarkLaf.setup();
            log("Theme initialized.");
        } catch (Exception e) {
            log("Failed to initialize NekoUI Update Notifier");
        }

        // Create updater config file if not exists
        File configFile = new File("updater-config.json");
        if (!configFile.exists()) {
            Files.writeString(configFile.toPath(), "{\"autoUpdateEnabled\":false}", StandardCharsets.UTF_8);
            log("Created default config file on: " + configFile.getPath());
        }

        // Load config and determine if auto-update is enabled
        JsonObject config = JsonParser.parseReader(new FileReader(configFile)).getAsJsonObject();
        boolean autoUpdateEnabled = config.get("autoUpdateEnabled").getAsBoolean();
        log("Auto-update enabled: " + autoUpdateEnabled);

        // Launch update window UI
        SwingUtilities.invokeLater(() -> {
            try {
                if (!autoUpdateEnabled) {
                    createWindow(version, loader, mcVersion, modsPath);
                } else {
                    createAutoUpdateWindow(version, loader, mcVersion, modsPath);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    // Auto-update window
    private static void createAutoUpdateWindow(String version, String loader, String mcVersion, String modsPathArg) throws Exception {
        log("Creating auto update window...");
        JFrame frame = buildBaseWindow();
        JPanel shadowPanel = (JPanel) frame.getContentPane();

        // Title and message UI
        JLabel title = new JLabel("Updating NekoUI...", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel message = new JLabel("<html><div style='text-align: center;'>Please wait while the update is downloaded.</div></html>", SwingConstants.CENTER);
        message.setFont(new Font("SansSerif", Font.PLAIN, 14));
        message.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Close and auto-update toggle
        JButton closeButton = new JButton("✖");
        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        closePanel.add(closeButton);

        JPanel toggleAndDetailsPanel = new JPanel();
        toggleAndDetailsPanel.setLayout(new BorderLayout());

        // Load config and checkbox
        File configFile = new File("updater-config.json");
        JsonObject config = JsonParser.parseReader(new FileReader(configFile)).getAsJsonObject();
        JCheckBox autoUpdateToggle = new JCheckBox("Enable Auto-Update", config.get("autoUpdateEnabled").getAsBoolean());

        // Log details button
        JButton detailsButton = new JButton("Show Details");

        toggleAndDetailsPanel.add(autoUpdateToggle, BorderLayout.WEST);
        toggleAndDetailsPanel.add(detailsButton, BorderLayout.EAST);

        logScroll.setVisible(false);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        logScroll.setPreferredSize(new Dimension(860, 260));

        // Assemble layout
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(title);
        content.add(message);
        content.add(Box.createVerticalStrut(10));
        content.add(toggleAndDetailsPanel);
        content.add(Box.createVerticalStrut(10));
        content.add(logScroll);

        shadowPanel.add(closePanel, BorderLayout.NORTH);
        shadowPanel.add(content, BorderLayout.CENTER);

        // Start update in background
        updateNekoUI(version, loader, mcVersion, modsPathArg, title, message, content, frame);

        detailsButton.addActionListener(e -> {
            logVisible = !logVisible;
            logScroll.setVisible(logVisible);
            detailsButton.setText(logVisible ? "Hide Details" : "Show Details");

            frame.pack();
            frame.setShape(new RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight(), 20, 20));
            frame.setLocationRelativeTo(null);
        });

        // Handle close and toggle
        closeButton.addActionListener(e -> frame.dispose());
        autoUpdateToggle.addActionListener(e -> {
            config.addProperty("autoUpdateEnabled", autoUpdateToggle.isSelected());
            try {
                Files.writeString(configFile.toPath(), new Gson().toJson(config), StandardCharsets.UTF_8);
                log("Auto-update config changed to: " + autoUpdateToggle.isSelected());
            } catch (IOException ex) {
                log("Failed to save config: " + ex.getMessage());
            }
        });

        frame.pack();
        frame.setShape(new RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight(), 20, 20));
        frame.setLocationRelativeTo(null);

        fadeInFrame(frame);
        frame.setVisible(true);
    }

    // Standard update window
    private static void createWindow(String version, String loader, String mcVersion, String modsPathArg) throws Exception {
        log("Creating standard update window...");
        JFrame frame = buildBaseWindow();
        JPanel shadowPanel = (JPanel) frame.getContentPane();

        JLabel title = new JLabel("Update Available!", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel message = new JLabel("<html><div style='text-align: center;'><div><b>NekoUI " + version + "</b></div><div>is available for download.</div></div></html>", SwingConstants.CENTER);
        message.setFont(new Font("SansSerif", Font.PLAIN, 14));
        message.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Buttons
        JButton updateButton = new JButton("⬇ Update");
        JButton changelogButton = new JButton("📄 Changelog");
        JButton closeButton = new JButton("✖");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.add(updateButton);
        buttonPanel.add(changelogButton);

        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        closePanel.add(closeButton);

        JPanel toggleAndDetailsPanel = new JPanel();
        toggleAndDetailsPanel.setLayout(new BorderLayout());

        // Load config and checkbox
        File configFile = new File("updater-config.json");
        JsonObject config = JsonParser.parseReader(new FileReader(configFile)).getAsJsonObject();
        JCheckBox autoUpdateToggle = new JCheckBox("Enable Auto-Update", config.get("autoUpdateEnabled").getAsBoolean());

        // Log details button
        JButton detailsButton = new JButton("Show Details");

        toggleAndDetailsPanel.add(autoUpdateToggle, BorderLayout.WEST);
        toggleAndDetailsPanel.add(detailsButton, BorderLayout.EAST);

        logScroll.setVisible(false);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        logScroll.setPreferredSize(new Dimension(860, 240));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(title);
        content.add(message);
        content.add(Box.createVerticalStrut(10));
        content.add(buttonPanel);
        content.add(Box.createVerticalStrut(10));
        content.add(toggleAndDetailsPanel);
        content.add(Box.createVerticalStrut(10));
        content.add(logScroll);

        shadowPanel.add(closePanel, BorderLayout.NORTH);
        shadowPanel.add(content, BorderLayout.CENTER);

        // Allow window dragging
        final Point[] mouseDownCompCoords = {null};
        shadowPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mouseDownCompCoords[0] = e.getPoint();
            }
            public void mouseReleased(MouseEvent e) {
                mouseDownCompCoords[0] = null;
            }
        });
        shadowPanel.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point currCoords = e.getLocationOnScreen();
                frame.setLocation(currCoords.x - mouseDownCompCoords[0].x, currCoords.y - mouseDownCompCoords[0].y);
            }
        });

        closeButton.addActionListener(e -> frame.dispose());

        autoUpdateToggle.addActionListener(e -> {
            config.addProperty("autoUpdateEnabled", autoUpdateToggle.isSelected());
            try {
                Files.writeString(configFile.toPath(), new Gson().toJson(config), StandardCharsets.UTF_8);
                log("Auto-update config changed to: " + autoUpdateToggle.isSelected());
            } catch (IOException ex) {
                log("Failed to save config: " + ex.getMessage());
            }
        });

        detailsButton.addActionListener(e -> {
            logVisible = !logVisible;
            logScroll.setVisible(logVisible);
            detailsButton.setText(logVisible ? "Hide Details" : "Show Details");

            frame.pack();
            frame.setShape(new RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight(), 20, 20));
            frame.setLocationRelativeTo(null);
        });

        updateButton.addActionListener(e -> {
            log("Manual update triggered.");
            updateNekoUI(version, loader, mcVersion, modsPathArg, title, message, content, frame);
            buttonPanel.setVisible(false);
        });

        changelogButton.addActionListener(e -> {
            log("Opening changelog link...");
            frame.setAlwaysOnTop(false);
            try {
                Desktop.getDesktop().browse(new URI("https://github.strivo.xyz/nekoui-download/blob/main/Changes.md"));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Failed to open link:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                log("Failed to open changelog: " + ex.getMessage());
            }
        });

        frame.pack();
        frame.setShape(new RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight(), 20, 20));
        frame.setLocationRelativeTo(null);

        fadeInFrame(frame);
        frame.setVisible(true);
    }

    // Creates and configures the main update JFrame
    private static JFrame buildBaseWindow() {
        log("Building base window...");
        JFrame frame = new JFrame();
        frame.setUndecorated(true);
        frame.setAlwaysOnTop(true);
        frame.setMinimumSize(new Dimension(560, 240));
        frame.setBackground(new Color(0, 0, 0, 50));

        JPanel shadowPanel = new JPanel();
        shadowPanel.setBackground(UIManager.getColor("Panel.background"));
        shadowPanel.setLayout(new BorderLayout());
        shadowPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        frame.setContentPane(shadowPanel);

        return frame;
    }

    // Fades in the window smoothly
    private static void fadeInFrame(JFrame frame) {
        log("Fading in window...");
        frame.setOpacity(0f);
        new Thread(() -> {
            for (float i = 0f; i <= 1f; i += 0.05f) {
                final float opacity = i;
                SwingUtilities.invokeLater(() -> frame.setOpacity(opacity));
                try {
                    Thread.sleep(15);
                } catch (InterruptedException ignored) {}
            }
        }).start();
    }

    // Perform actual update logic: fetch, delete old, download new
    private static void updateNekoUI(String version, String loader, String mcVersion, String modsPathArg, JLabel title, JLabel message, JPanel content, JFrame frame) {
        log("Starting update NekoUI process...");
        title.setText("Updating NekoUI...");
        message.setText("<html><div style='text-align: center;'>Please wait while the update is downloaded.</div></html>");

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        content.add(Box.createVerticalStrut(10));
        content.add(progressBar);
        frame.revalidate();

        new Thread(() -> {
            try {
                // Query modrinth API for latest version
                String modrinthJson = new String(new URI("https://api.modrinth.com/v2/project/EZpbRipP/version").toURL().openStream().readAllBytes());
                JSONArray versions = new JSONArray(modrinthJson);
                String normalizedVersion = version.replaceFirst("^v", "");
                String[] result = findDownloadUrl(versions, loader, mcVersion, normalizedVersion);
                if (result == null) {
                    log("No compatible file found!");
                    throw new Exception("No compatible file found");
                }

                // Remove old NekoUI
                File modsDir = modsPathArg != null ? new File(modsPathArg) : resolveModsDirectory();
                if (modsDir.exists()) {
                    for (File file : Objects.requireNonNull(modsDir.listFiles())) {
                        if (file.getName().toLowerCase().startsWith("nekoui") && file.getName().endsWith(".jar")) {
                            log("Deleting old nekoui version!");
                            file.delete();
                        }
                    }
                }

                // Download and save new file
                File targetFile = new File(modsDir, result[1]);
                downloadFileWithProgress(result[0], targetFile.getAbsolutePath(), progressBar);

                SwingUtilities.invokeLater(() -> {
                    log("Update NekoUI Complete!");
                    message.setText("<html><div style='text-align: center;'>Download complete.<br>Restart Minecraft to apply update.</div></html>");
                });
            } catch (Exception ex) {
                log("Failed to update NekoUI: " + ex.getMessage());
                SwingUtilities.invokeLater(() -> message.setText("<html><div style='text-align: center; color:red;'>Failed: " + ex.getMessage() + "</div></html>"));
            }
        }).start();
    }

    // Try to detect mod directory for launcher-agnostic support
    private static File resolveModsDirectory() {
        log("Resolving mods directory...");
        try {
            File jarPath = new File(UpdateNotifier.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
            File instanceMods = new File(jarPath, "mods");
            if (instanceMods.exists() && instanceMods.isDirectory()) {
                log("Using instance mods directory: " + instanceMods.getAbsolutePath());
                return instanceMods;
            }
        } catch (Exception e) {
            log("Failed to resolve instance mods directory: " + e.getMessage());
        }
        File fallback = new File(System.getProperty("user.home"), ".minecraft/mods");
        log("Falling back to: " + fallback.getAbsolutePath());
        return fallback;
    }

    // Download a file and update progress bar in UI
    private static void downloadFileWithProgress(String fileURL, String savePath, JProgressBar progressBar) throws Exception {
        log("Starting download from: " + fileURL);
        log("Saving to: " + savePath);
        try {
            URL url = new URI(fileURL).toURL();
            URLConnection connection = url.openConnection();
            int fileSize = connection.getContentLength();
            log("File size: " + fileSize + " bytes");

            try (BufferedInputStream input = new BufferedInputStream(connection.getInputStream());
                 FileOutputStream output = new FileOutputStream(savePath)) {

                byte[] dataBuffer = new byte[1024];
                int bytesRead;
                long totalRead = 0;

                while ((bytesRead = input.read(dataBuffer, 0, 1024)) != -1) {
                    output.write(dataBuffer, 0, bytesRead);
                    totalRead += bytesRead;
                    final int percent = fileSize > 0 ? (int) (totalRead * 100 / fileSize) : 0;
                    SwingUtilities.invokeLater(() -> {
                        progressBar.setValue(percent);
                        progressBar.setString(percent + "%");
                        log("Downloading NekoUI: " + percent + "%");
                    });
                }
                log("Download complete. Total downloaded: " + totalRead + " bytes.");
            }
        } catch (Exception e) {
            log("Download failed: " + e.getMessage());
            throw new Exception("Failed to download file: " + e.getMessage());
        }
    }

    // Find the download URL and filename from Modrinth JSON based on loader/game version
    private static String[] findDownloadUrl(JSONArray versions, String currentLoader, String gameVersion, String targetVersion) {
        log("Searching for compatible file with loader=" + currentLoader + ", gameVersion=" + gameVersion + ", targetVersion=" + targetVersion);
        for (int i = 0; i < versions.length(); i++) {
            JSONObject version = versions.getJSONObject(i);
            String versionName = version.optString("version_number", "");
            log("Checking version entry " + i + ": " + versionName);
            if (!versionName.contains(targetVersion)) continue;

            JSONArray gameVersions = version.getJSONArray("game_versions");
            JSONArray loaders = version.getJSONArray("loaders");

            boolean loaderMatch = false;
            for (int j = 0; j < loaders.length(); j++) {
                if (loaders.getString(j).equalsIgnoreCase(currentLoader)) {
                    loaderMatch = true;
                    break;
                }
            }

            boolean versionMatch = false;
            for (int j = 0; j < gameVersions.length(); j++) {
                if (gameVersions.getString(j).equalsIgnoreCase(gameVersion)) {
                    versionMatch = true;
                    break;
                }
            }

            if (loaderMatch && versionMatch) {
                JSONArray files = version.getJSONArray("files");
                if (!files.isEmpty()) {
                    JSONObject file = files.getJSONObject(0);
                    String url = file.getString("url");
                    String filename = file.getString("filename");
                    log("Found compatible file: " + filename);
                    return new String[] { url, filename };
                } else {
                    log("No files in matching version entry.");
                    }
            }
            log("Checking version entry " + i);
        }
        log("No compatible file found.");
        return null;
    }

    private static void log(String text) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(text + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
}
