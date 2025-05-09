/**
 * Provides a modern NetworkTables utility, serving as a replacement to {@code SmartDashboard}.
 * <p>
 * Badgerlog serves as a modern, alternative to SmartDashboard with enhanced features for:
 *
 * <ul>
 *   <li>Annotation-driven configuration of dashboard fields</li>
 *   <li>Automatic type conversion and unit handling</li>
 *   <li>Multiple struct serialization strategies</li>
 *   <li>Complex data type support including WPILib geometry classes</li>
 * </ul>
 * <p>
 * Core components:
 * <ol>
 *   <li>{@link badgerlog.Dashboard} - Main entry point for dashboard operations</li>
 *   <li>{@link badgerlog.entry} subsystem - Annotation processing and field configuration</li>
 *   <li>{@link badgerlog.networktables} subsystem - NetworkTables integration layer</li>
 * </ol>
 * <p>
 * Key features:
 * <ul>
 *   <li>Declarative programming model using annotations. Examples include:
 *     <ul>
 *       <li>{@link badgerlog.entry.Entry} - Mark fields for NT publishing/subscribing</li>
 *       <li>{@link badgerlog.entry.handlers.Key} - Custom NT keys</li>
 *       <li>{@link badgerlog.entry.handlers.UnitConversion} - Unit specifications</li>
 *     </ul>
 *   </li>
 *   <li>Three struct handling modes via {@link badgerlog.StructOptions}:
 *     <ul>
 *       <li>Direct struct serialization</li>
 *       <li>Subtable</li>
 *       <li>Type-mapped conversion</li>
 *     </ul>
 *   </li>
 *   <li>Automatic reflection-based field registration</li>
 *   <li>Compile-time validation through {@link badgerlog.AnnotationProcessor}</li>
 * </ul>
 */
package badgerlog;