package com.github.kindrat.cassandra.client.ui.window.editor.tables;

import com.github.kindrat.cassandra.client.i18n.MessageByLocaleService;
import com.github.kindrat.cassandra.client.properties.UIProperties;
import com.github.kindrat.cassandra.client.ui.MainController;
import com.github.kindrat.cassandra.client.ui.editor.TableListContext;
import com.github.kindrat.cassandra.client.util.UIUtil;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.SplitPane;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.AnchorPane;

import java.util.function.Consumer;

public class TablePanel extends AnchorPane {
    private final TableListView tableListView;
    private final TableButtons buttons;
    private final ContextMenu tableContext;
    private final MainController controller;

    public TablePanel(UIProperties uiProperties, MessageByLocaleService localeService, MainController controller) {
        this.controller = controller;
        setPrefHeight(uiProperties.getTablesPrefHeight());
        setPrefWidth(uiProperties.getTablesPrefWidth());

        buttons = new TableButtons();
        tableListView = new TableListView();
        tableListView.onMouseClick(buttons::enableButtons, buttons::disableButtons);
        tableListView.setOnContextMenuRequested(this::onTableContextMenu);

        SplitPane splitPane = splitPane(uiProperties);
        UIUtil.fillParent(splitPane);
        getChildren().add(splitPane);

        tableContext = new TableListContext(localeService, this::tryLoadDDL, this::tryLoadData);
    }

    public void showTables(ObservableList<String> tables) {
        tableListView.showTables(tables);
    }

    public void clear() {
        tableContext.hide();
        tableListView.clear();
    }

    public void setNewValueListener(Consumer<String> newSelectedTableListener) {
        tableListView.onNewValueSelected((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newSelectedTableListener.accept(newValue);
            }
            tableContext.hide();
        });
    }

    private SplitPane splitPane(UIProperties uiProperties) {
        SplitPane splitPane = new SplitPane(tableListView, buttons);
        splitPane.setDividerPositions(uiProperties.getTablesDividerPosition());
        splitPane.setFocusTraversable(false);
        splitPane.setMaxHeight(40);
        splitPane.setPrefHeight(40);
        splitPane.setPrefWidth(160);
        splitPane.setScaleShape(false);
        splitPane.setOrientation(Orientation.VERTICAL);
        return splitPane;
    }

    private void tryLoadDDL() {
        tableListView.getSelectedTable().ifPresent(controller::showDDLForTable);
    }

    private void tryLoadData() {
        tableListView.getSelectedTable().ifPresent(controller::showDataForTable);
    }

    private void onTableContextMenu(ContextMenuEvent event) {
        tableListView.getSelectedTable()
                .ifPresent(table -> tableContext.show(this, event.getScreenX(), event.getScreenY()));
    }
}
