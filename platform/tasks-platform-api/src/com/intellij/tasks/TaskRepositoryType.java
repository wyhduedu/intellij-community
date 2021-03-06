// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.tasks;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.extensions.ExtensionPointChangeListener;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import com.intellij.tasks.config.TaskRepositoryEditor;
import com.intellij.util.Consumer;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * The main extension point for issue tracking integration.
 *
 * @author Dmitry Avdeev
 */
public abstract class TaskRepositoryType<T extends TaskRepository> implements TaskRepositorySubtype, Comparable<TaskRepositoryType> {

  public static final ExtensionPointName<TaskRepositoryType> EP_NAME = new ExtensionPointName<>("com.intellij.tasks.repositoryType");

  public static TaskRepositoryType<?> @NotNull [] getRepositoryTypes() {
    return EP_NAME.getExtensions();
  }

  public static @NotNull List<Class<?>> getRepositoryClasses() {
    return ContainerUtil.map(getRepositoryTypes(), TaskRepositoryType::getRepositoryClass);
  }

  public static <T> void addEPListChangeListener(@NotNull Disposable disposable, @NotNull ExtensionPointChangeListener listener) {
    EP_NAME.addExtensionPointListener(listener, disposable);
  }

  @Override
  @NotNull
  public abstract String getName();

  @Override
  @NotNull
  public abstract Icon getIcon();

  @Nullable
  public String getAdvertiser() { return null; }

  @NotNull
  public abstract TaskRepositoryEditor createEditor(T repository, Project project, Consumer<T> changeListener);

  public List<TaskRepositorySubtype> getAvailableSubtypes() {
    return Collections.singletonList(this);
  }

  @NotNull
  public TaskRepository createRepository(TaskRepositorySubtype subtype) {
    return subtype.createRepository();
  }

  @Override
  @NotNull
  public abstract TaskRepository createRepository();

  public abstract Class<T> getRepositoryClass();

  /**
   * @return states that can be set by {@link TaskRepository#setTaskState(Task, CustomTaskState)}
   * @deprecated Use {@link TaskRepository#getAvailableTaskStates(Task)} instead.
   */
  @Deprecated
  public EnumSet<TaskState> getPossibleTaskStates() {
    return EnumSet.noneOf(TaskState.class);
  }

  public int getSortOrder() {
    return 0;
  }

  @Override
  public int compareTo(@NotNull TaskRepositoryType other) {
    return other.getSortOrder() - this.getSortOrder();
  }
}
