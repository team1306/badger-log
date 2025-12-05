package frc.robot;

import badgerlog.Dashboard;
import edu.wpi.first.hal.HAL;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.PubSubOption;
import edu.wpi.first.networktables.IntegerTopic;
import edu.wpi.first.networktables.DoubleTopic;
import edu.wpi.first.networktables.TopicInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class BadgerLogTest {
    private NetworkTableInstance ntInstance;

    @BeforeEach
    void setUp() {
        HAL.initialize(500, 0);
        ntInstance = NetworkTableInstance.getDefault();
    }

    @AfterEach
    void tearDown() {

    }

    void PrintAllTopics() {
        for(TopicInfo ti : ntInstance.getTopicInfo()) {
            System.out.println(ti.name + " : " + ti.typeStr);
        }
    }

    @Test
    void testFieldPublishingAndSubscription() throws InterruptedException {
        System.out.println("Starting FieldTest");
        PubSubOption[] options = new PubSubOption[] {};

        
        /********* Test static publishing ***********/
        // Make sure FieldsTest is initialized.
        int basicIntegerValue = FieldsTest.basicInteger;
        NetworkTable staticInstanceTable = ntInstance.getTable("/BadgerLog/Auto - ");
        IntegerTopic basicIntegerTopic = staticInstanceTable.getIntegerTopic("basicInteger");
         // Check basic integer is published
        assertThat(basicIntegerTopic.exists()).isTrue();
        assertThat(basicIntegerTopic.getEntry(-1, options).get()).isEqualTo(basicIntegerValue);


         /********* Test named instance publishing ***********/
        // Create an instance
        FieldsTest fieldsTest = new FieldsTest("FooInstance");
        
        // Debugging aid.
        PrintAllTopics();
         
        // Check instance double instance is published.
        NetworkTable instanceTable = ntInstance.getTable("/BadgerLog/Auto - FooInstance");
        DoubleTopic heightTopic = instanceTable.getDoubleTopic("height");
        
        assertThat(heightTopic.exists()).isTrue();
        assertThat(heightTopic.getEntry(-1, options).get()).isEqualTo(0.254); // 10 inches to meters

    
        // TODO: Figure out how to test structs
        // assertThat(instanceTable.getSubTable("rotation2d").getDoubleTopic("degrees").getEntry(-1, options).get()).isEqualTo(1306.0);


        assertThat(instanceTable.getSubTable("record").getDoubleTopic("value").getEntry(-1, options).get()).isEqualTo(3.4);
        assertThat(instanceTable.getSubTable("record").getIntegerTopic("count").getEntry(-1, options).get()).isEqualTo(3);
        assertThat(instanceTable.getDoubleTopic("getBasicInteger").getEntry(-1, options).get()).isEqualTo(1 * 0.333, within(0.001));

/* The rest of this code is AI generated and needs some work.

        // 2. Call update and check for new published values
        double oldHeight = fieldsTest.height.in(edu.wpi.first.units.Units.Meters);
        double oldRotation = fieldsTest.rotation2d.getDegrees();
        int oldBasicInteger = FieldsTest.basicInteger;

        fieldsTest.update();
        Dashboard.update();

        assertThat(instanceTable.getIntegerTopic("basicInteger").get()).isNotEqualTo(oldBasicInteger);
        assertThat(instanceTable.getDoubleTopic("height").get()).isNotEqualTo(oldHeight);
        assertThat(instanceTable.getSubTable("rotation2d").getDoubleTopic("degrees").get()).isNotEqualTo(oldRotation);
        assertThat(instanceTable.getSubTable("record").getDoubleTopic("value").get()).isNotEqualTo(3.4);
        assertThat(instanceTable.getSubTable("record").getIntegerTopic("count").get()).isNotEqualTo(3);
        assertThat(instanceTable.getDoubleTopic("getBasicInteger").get()).isNotEqualTo(1 * 0.333, within(0.001));

        // 3. Change subscribed values in NT and check if instance properties are updated
        instanceTable.getDoubleTopic("AutoWaitTime").set(5.0);
        instanceTable.getDoubleTopic("Modules/Test/Module P").set(0.5);
        NetworkTable poseTable = instanceTable.getSubTable("robotPose");
        poseTable.getEntry("x").setDouble(1.0); // in
        poseTable.getEntry("y").setDouble(2.0); // in
        poseTable.getEntry("rotation").setDouble(Math.PI / 2); // radian

        Dashboard.update();

        assertThat(fieldsTest.waitTime).isEqualTo(5.0);
        assertThat(fieldsTest.moduleP).isEqualTo(0.5);

        // Pose2d is in meters and radians internally
        Pose2d expectedPose = new Pose2d(1.0 * 0.0254, 2.0 * 0.0254, Math.PI / 2);
        assertThat(fieldsTest.robotPose.getX()).isEqualTo(expectedPose.getX(), within(0.001));
        assertThat(fieldsTest.robotPose.getY()).isEqualTo(expectedPose.getY(), within(0.001));
        assertThat(fieldsTest.robotPose.getRotation().getRadians()).isEqualTo(expectedPose.getRotation().getRadians(), within(0.001));

        */
    }
}
