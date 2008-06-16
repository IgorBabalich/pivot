/*
 * Copyright (c) 2008 VMware, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pivot.wtk;

import pivot.collections.ArrayList;
import pivot.collections.Sequence;
import pivot.util.ListenerList;
import pivot.wtk.skin.TablePaneSkin;

/**
 *
 * @author tvolkert
 */
public class TablePane extends Container {
    public static class Row {
        private TablePane tablePane = null;

        private int height = 0;
        private boolean relative = false;
        private boolean selected = false;

        public Row(int height) {
            this(height, false);
        }

        public Row(int height, boolean relative) {
            this(height, relative, false);
        }

        public Row(int height, boolean relative, boolean selected) {
            this.height = height;
            this.relative = relative;
            this.selected = selected;
        }

        /**
         * Returns the table pane with which this row is associated.
         *
         * @return
         * The row's table pane, or <tt>null</tt> if the row does not
         * currently belong to a table.
         */
        public TablePane getTablePane() {
            return tablePane;
        }

        /**
         * Sets the table pane with which this row is associated.
         *
         * @param tablePane
         * The row's table pane, or <tt>null</tt> if the row does not
         * currently belong to a table.
         */
        protected void setTablePane(TablePane tablePane) {
            this.tablePane = tablePane;
        }

        /**
         * Returns the row height.
         *
         * @return
         * The height of the row.
         */
        public int getHeight() {
            return height;
        }

        /**
         * Returns the relative flag.
         *
         * @return
         * <tt>true</tt> if the row height is relative, <tt>false</tt> if it
         * is fixed.
         */
        public boolean isRelative() {
            return relative;
        }

        /**
         * Sets the row height.
         *
         * @param height
         * The height of the row.
         *
         * @param relative
         * <tt>true</tt> if the row height is relative, <tt>false</tt> if it
         * is fixed.
         */
        public void setHeight(int height, boolean relative) {
            int previousHeight = this.height;
            boolean previousRelative = this.relative;

            if (previousHeight != height
                || previousRelative != relative) {
                this.height = height;
                this.relative = relative;

                if (tablePane != null) {
                    tablePane.tablePaneListeners.rowHeightChanged(tablePane,
                        tablePane.rows.indexOf(this), previousHeight,
                        previousRelative);
                }
            }
        }

        /**
         * Returns the selected flag.
         *
         * @return
         * <tt>true</tt> if the row is selected, <tt>false</tt> if it is not
         */
        public boolean isSelected() {
            return selected;
        }

        /**
         * Sets the selected flag.
         *
         * @param selected
         * <tt>true</tt> to set the row as selected, <tt>false</tt> to set
         * it as not selected
         */
        public void setSelected(boolean selected) {
            if (selected != this.selected) {
                this.selected = selected;

                if (tablePane != null) {
                    tablePane.tablePaneListeners.rowSelectedChanged(tablePane,
                        tablePane.rows.indexOf(this));
                }
            }
        }
    }

    public static class Column {
        private TablePane tablePane = null;

        private int width = 0;
        private boolean relative = false;
        private Object headerData = null;
        private boolean selected = false;

        public Column(int width) {
            this(width, false, null, false);
        }

        public Column(int width, Object headerData) {
            this(width, false, headerData, false);
        }

        public Column(int width, boolean relative) {
            this(width, relative, null, false);
        }

        public Column(int width, boolean relative, Object headerData) {
            this(width, relative, headerData, false);
        }

        public Column(int width, boolean relative, Object headerData, boolean selected) {
            this.width = width;
            this.relative = relative;
            this.headerData = headerData;
            this.selected = selected;
        }

        /**
         * Returns the table pane with which this column is associated.
         *
         * @return
         * The column's table pane, or <tt>null</tt> if the column does not
         * currently belong to a table.
         */
        public TablePane getTablePane() {
            return tablePane;
        }

        /**
         * Sets the table pane with which this column is associated.
         *
         * @param tablePane
         * The column's table pane, or <tt>null</tt> if the column does not
         * currently belong to a table.
         */
        protected void setTablePane(TablePane tablePane) {
            this.tablePane = tablePane;
        }

        /**
         * Returns the column width.
         *
         * @return
         * The width of the column.
         */
        public int getWidth() {
            return width;
        }

        /**
         * Returns the relative flag.
         *
         * @return
         * <tt>true</tt> if the column width is relative, <tt>false</tt> if it
         * is fixed.
         */
        public boolean isRelative() {
            return relative;
        }

        /**
         * Sets the column width.
         *
         * @param width
         * The width of the column.
         *
         * @param relative
         * <tt>true</tt> if the column width is relative, <tt>false</tt> if it
         * is fixed.
         */
        public void setWidth(int width, boolean relative) {
            int previousWidth = this.width;
            boolean previousRelative = this.relative;

            if (previousWidth != width
                || previousRelative != relative) {
                this.width = width;
                this.relative = relative;

                if (tablePane != null) {
                    tablePane.tablePaneListeners.columnWidthChanged(tablePane,
                        tablePane.columns.indexOf(this), previousWidth,
                        previousRelative);
                }
            }
        }

        /**
         * Returns the column header data.
         *
         * @return
         * The column header data, or <tt>null</tt> if the column has no
         * header data.
         */
        public Object getHeaderData() {
            return headerData;
        }

        /**
         * Sets the column header data.
         *
         * @param headerData
         * The column header data, or <tt>null</tt> for no header data.
         */
        public void setHeaderData(Object headerData) {
            Object previousHeaderData = this.headerData;

            if (previousHeaderData != headerData) {
                this.headerData = headerData;

                if (tablePane != null) {
                    tablePane.tablePaneListeners.columnHeaderDataChanged(tablePane,
                        tablePane.columns.indexOf(this), previousHeaderData);
                }
            }
        }

        /**
         * Returns the selected flag.
         *
         * @return
         * <tt>true</tt> if the column is selected, <tt>false</tt> if it is not
         */
        public boolean isSelected() {
            return selected;
        }

        /**
         * Sets the selected flag.
         *
         * @param selected
         * <tt>true</tt> to set the column as selected, <tt>false</tt> to set
         * it as not selected
         */
        public void setSelected(boolean selected) {
            if (selected != this.selected) {
                this.selected = selected;

                if (tablePane != null) {
                    tablePane.tablePaneListeners.columnSelectedChanged(tablePane,
                        tablePane.columns.indexOf(this));
                }
            }
        }
    }

    /**
     * Table pane skin interface. Table pane skins must implement
     * this interface to facilitate additional communication between the
     * component and the skin.
     *
     * @author tvolkert
     */
    public interface Skin extends pivot.wtk.Skin {
        public int getRowAt(int y);
        public Rectangle getRowBounds(int row);
        public int getColumnAt(int x);
        public Rectangle getColumnBounds(int column);
    }

    /**
     * Class that manages a table pane's row list. Callers get access to the
     * row sequence via {@link TablePane#getRows()}.
     *
     * @author tvolkert
     */
    public final class RowSequence implements Sequence<Row> {
        public int add(Row row) {
            int i = getLength();
            insert(row, i);

            return i;
        }

        public void insert(Row row, int index) {
            if (row == null) {
                throw new IllegalArgumentException("row is null.");
            }

            if (row.getTablePane() != null) {
                throw new IllegalArgumentException
                    ("row is already in use by another table pane.");
            }

            rows.insert(row, index);
            row.setTablePane(TablePane.this);

            // Add capacity to our cell component array
            ArrayList<Component> rowData = new ArrayList<Component>(columns.getLength());
            cellData.insert(rowData, index);

            for (int i = 0, n = columns.getLength(); i < n; i++) {
                rowData.add(null);
            }

            // Notify listeners
            tablePaneListeners.rowInserted(TablePane.this, index);
        }

        public Row update(int index, Row row) {
            throw new UnsupportedOperationException();
        }

        public int remove(Row row) {
            int index = indexOf(row);
            if (index != -1) {
                remove(index, 1);
            }

            return index;
        }

        public Sequence<Row> remove(int index, int count) {
            Sequence<Row> removed = rows.remove(index, count);

            if (count > 0) {
                for (int i = 0, n = removed.getLength(); i < n; i++) {
                    removed.get(i).setTablePane(null);
                }

                Container.ComponentSequence components = getComponents();

                // Reduce capacity in our cell component array
                Sequence<ArrayList<Component>> removedRowData = cellData.remove
                    (index, count);

                for (int i = 0, n = removedRowData.getLength(); i < n; i++) {
                    ArrayList<Component> removedComponents = removedRowData.get(i);

                    internalRemoval = true;
                    // Remove any components that were in those rows
                    for (int j = 0, m = removedComponents.getLength(); j < m; j++) {
                       Component removedComponent = removedComponents.get(j);

                       if (removedComponent != null) {
                          components.remove(removedComponent);
                       }
                    }
                    internalRemoval = false;
                }

                tablePaneListeners.rowsRemoved(TablePane.this, index, removed);
            }

            return removed;
        }

        public Row get(int index) {
            return rows.get(index);
        }

        public int indexOf(Row row) {
            return rows.indexOf(row);
        }

        public int getLength() {
            return rows.getLength();
        }
    }

    /**
     * Class that manages a table pane's column list. Callers get access to the
     * column sequence via {@link TablePane#getColumns()}.
     *
     * @author tvolkert
     */
    public final class ColumnSequence implements Sequence<Column> {
        public int add(Column column) {
            int i = getLength();
            insert(column, i);

            return i;
        }

        public void insert(Column column, int index) {
            if (column == null) {
                throw new IllegalArgumentException("column is null.");
            }

            if (column.getTablePane() != null) {
                throw new IllegalArgumentException
                    ("column is already in use by another table pane.");
            }

            columns.insert(column, index);
            column.setTablePane(TablePane.this);

            // Add capacity to our cell component array
            for (int i = 0, n = rows.getLength(); i < n; i++) {
               cellData.get(i).insert(null, index);
            }

            // Notify listeners
            tablePaneListeners.columnInserted(TablePane.this, index);
        }

        public Column update(int index, Column column) {
            throw new UnsupportedOperationException();
        }

        public int remove(Column column) {
            int index = indexOf(column);
            if (index != -1) {
                remove(index, 1);
            }

            return index;
        }

        public Sequence<Column> remove(int index, int count) {
            Sequence<Column> removed = columns.remove(index, count);

            if (count > 0) {
                for (int i = 0, n = removed.getLength(); i < n; i++) {
                    removed.get(i).setTablePane(null);
                }

                Container.ComponentSequence components = getComponents();

                // Reduce capacity in our cell component array
                for (int i = 0, n = rows.getLength(); i < n; i++) {
                    Sequence<Component> removedComponents = cellData.get(i).remove
                        (index, count);

                    internalRemoval = true;
                    // Remove any components that were in those columns
                    for (int j = 0, m = removedComponents.getLength(); j < m; j++) {
                       Component removedComponent = removedComponents.get(j);

                       if (removedComponent != null) {
                          components.remove(removedComponent);
                       }
                    }
                    internalRemoval = false;
                }

                tablePaneListeners.columnsRemoved(TablePane.this, index, removed);
            }

            return removed;
        }

        public Column get(int index) {
            return columns.get(index);
        }

        public int indexOf(Column column) {
            return columns.indexOf(column);
        }

        public int getLength() {
            return columns.getLength();
        }
    }

    /**
     * Internal listener list.
     */
    private class TablePaneListenerList extends ListenerList<TablePaneListener>
        implements TablePaneListener {
        public void rowInserted(TablePane tablePane, int index) {
            for (TablePaneListener listener : this) {
                listener.rowInserted(tablePane, index);
            }
        }

        public void rowsRemoved(TablePane tablePane, int index,
            Sequence<TablePane.Row> rows) {
            for (TablePaneListener listener : this) {
                listener.rowsRemoved(tablePane, index, rows);
            }
        }

        public void rowHeightChanged(TablePane tablePane, int index,
            int previousHeight, boolean previousRelative) {
            for (TablePaneListener listener : this) {
                listener.rowHeightChanged(tablePane, index, previousHeight,
                    previousRelative);
            }
        }

        public void rowSelectedChanged(TablePane tablePane, int index) {
            for (TablePaneListener listener : this) {
                listener.rowSelectedChanged(tablePane, index);
            }
        }

        public void columnInserted(TablePane tablePane, int index) {
            for (TablePaneListener listener : this) {
                listener.columnInserted(tablePane, index);
            }
        }

        public void columnsRemoved(TablePane tablePane, int index,
            Sequence<TablePane.Column> columns) {
            for (TablePaneListener listener : this) {
                listener.columnsRemoved(tablePane, index, columns);
            }
        }

        public void columnHeaderDataChanged(TablePane tablePane, int index,
            Object previousHeaderData) {
            for (TablePaneListener listener : this) {
                listener.columnHeaderDataChanged(tablePane, index, previousHeaderData);
            }
        }

        public void columnWidthChanged(TablePane tablePane, int index,
            int previousWidth, boolean previousRelative) {
            for (TablePaneListener listener : this) {
                listener.columnWidthChanged(tablePane, index, previousWidth,
                    previousRelative);
            }
        }

        public void columnSelectedChanged(TablePane tablePane, int index) {
            for (TablePaneListener listener : this) {
                listener.columnSelectedChanged(tablePane, index);
            }
        }

        public void cellComponentChanged(TablePane tablePane, int row, int column,
            Component previousComponent) {
            for (TablePaneListener listener : this) {
                listener.cellComponentChanged(tablePane, row, column, previousComponent);
            }
        }
    }

    private ArrayList<Row> rows;
    private RowSequence rowSequence = new RowSequence();

    private ArrayList<Column> columns;
    private ColumnSequence columnSequence = new ColumnSequence();

    private ArrayList<ArrayList<Component>> cellData;

    private boolean internalRemoval = false;

    private TablePaneListenerList tablePaneListeners = new TablePaneListenerList();

    public static final Attribute ROW_SPAN_ATTRIBUTE = new Attribute(Integer.class);
    public static final Attribute COLUMN_SPAN_ATTRIBUTE = new Attribute(Integer.class);

    public static final Attribute MARGIN_ATTRIBUTE = new Attribute(Insets.class);

    /**
     * Creates a new <tt>TablePane</tt> with empty row and column sequences.
     */
    public TablePane() {
        this(new ArrayList<Row>(), new ArrayList<Column>());
    }

    /**
     * Creates a new <tt>TablePane</tt> with the specified row and column
     * sequences.
     *
     * @param rows
     * The row sequence to use. A copy of this sequence will be made
     *
     * @param columns
     * The column sequence to use. A copy of this sequence will be made
     */
    public TablePane(Sequence<Row> rows, Sequence<Column> columns) {
        if (rows == null) {
            throw new IllegalArgumentException("rows is null");
        }

        if (columns == null) {
            throw new IllegalArgumentException("columns is null");
        }

        this.rows = new ArrayList<Row>(rows);
        this.columns = new ArrayList<Column>(columns);

        cellData = new ArrayList<ArrayList<Component>>(rows.getLength());
        for (int i = 0, n = rows.getLength(); i < n; i++) {
            ArrayList<Component> rowData = new ArrayList<Component>(columns.getLength());
            cellData.add(rowData);

            for (int j = 0, m = columns.getLength(); j < m; j++) {
                rowData.add(null);
            }
        }

        if (getClass() == TablePane.class) {
            setSkinClass(TablePaneSkin.class);
        }
    }

    /**
     * Overrides the base method implementation to throw an error if the skin
     * class is not an instance of {@link TablePane.Skin}. Skin authors writing
     * skins for <tt>TablePane</tt> must implement this interface.
     *
     * @param skinClass
     * The type of the skin to install.
     */
    @Override
    public void setSkinClass(Class<? extends pivot.wtk.Skin> skinClass) {
        if (!TablePane.Skin.class.isAssignableFrom(skinClass)) {
            throw new IllegalArgumentException("Skin class must implement "
                + TablePane.Skin.class.getName());
        }

        super.setSkinClass(skinClass);
    }

    /**
     * Returns the table pane row sequence.
     *
     * @return
     * The table pane row sequence
     */
    public RowSequence getRows() {
        return rowSequence;
    }

    /**
     * Returns the index of the row at a given location.
     *
     * @param y
     * The y-coordinate of the row to identify.
     *
     * @return
     * The row index, or <tt>-1</tt> if there is no row at the given
     * y-coordinate.
     */
    public int getRowAt(int y) {
        TablePane.Skin tablePaneSkin = (TablePane.Skin)getSkin();
        return tablePaneSkin.getRowAt(y);
    }

    /**
     * Returns the table pane column sequence.
     *
     * @return
     * The table pane column sequence
     */
    public ColumnSequence getColumns() {
        return columnSequence;
    }

    /**
     * Returns the index of the column at a given location.
     *
     * @param x
     * The x-coordinate of the column to identify.
     *
     * @return
     * The column index, or <tt>-1</tt> if there is no column at the given
     * x-coordinate.
     */
    public int getColumnAt(int x) {
        TablePane.Skin tablePaneSkin = (TablePane.Skin)getSkin();
        return tablePaneSkin.getColumnAt(x);
    }

    /**
     * Gets the component at the specified cell in this table pane.
     *
     * @param row
     * The row index of the cell
     *
     * @param column
     * The column index of the cell
     *
     * @return
     * The component in the specified cell, or <tt>null</tt> if the cell is
     * empty
     */
    public Component getCellComponent(int row, int column) {
        if (row < 0 || row >= rows.getLength()
            || column < 0 || column >= columns.getLength()) {
            throw new IndexOutOfBoundsException();
        }

        return cellData.get(row).get(column);
    }

    /**
     * Sets the component at the specified cell in this table pane.
     *
     * @param row
     * The row index of the cell
     *
     * @param column
     * The column index of the cell
     *
     * @param component
     * The component to place in the specified cell, or <tt>null</tt> to empty
     * the cell
     */
    public void setCellComponent(int row, int column, Component component) {
        if (row < 0 || row >= rows.getLength()
            || column < 0 || column >= columns.getLength()) {
            throw new IndexOutOfBoundsException();
        }

        Component previousComponent = getCellComponent(row, column);

        if (previousComponent != component) {
            Container.ComponentSequence components = getComponents();
            ArrayList<Component> rowData = cellData.get(row);

            if (component != null) {
                if (component.getParent() != null) {
                    throw new IllegalArgumentException("Component already has a parent.");
                }

                // Add the component
                components.add(component);
            }

            // Set the component as the new cell component (note that we
            // set the new component before removing the old one so two
            // cell component change events don't get fired)
            rowData.update(column, component);

            // Remove any previous component
            if (previousComponent != null) {
                internalRemoval = true;
                components.remove(previousComponent);
                internalRemoval = false;
            }

            tablePaneListeners.cellComponentChanged(this, row, column, previousComponent);
        }
    }

    /**
     * Overrides the base method to check whether or not a cell component is
     * being removed, and fires the appropriate event in that case.
     *
     * @param index
     * The index at which components were removed
     *
     * @param count
     * The number of components removed
     *
     * @return
     * The sequence of components that were removed
     */
    @Override
    protected Sequence<Component> removeComponents(int index, int count) {
        // Call the base method to remove the components
        Sequence<Component> removed = super.removeComponents(index, count);

        // Ensure that the appropriate instance variable is cleared if the
        // component being removed maps to a cell component
        if (!internalRemoval) {
            for (int i = 0, n = removed.getLength(); i < n; i++) {
                Component component = removed.get(i);

                for (int j = 0, m = rows.getLength(); j < m; j++) {
                    ArrayList<Component> rowData = cellData.get(j);

                    for (int k = 0, l = columns.getLength(); k < l; k++) {
                        if (rowData.get(k) == component) {
                            rowData.update(k, null);
                            tablePaneListeners.cellComponentChanged(this, j, k,
                                component);
                        }
                    }
                }
            }
        }

        return removed;
    }

    /**
     * Gets this component's table pane listener list.
     *
     * @return
     * The table pane listeners on this component
     */
    public ListenerList<TablePaneListener> getTablePaneListeners() {
        return tablePaneListeners;
    }
}
