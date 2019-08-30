package flavor.pie.example.data;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.data.Has;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.UUID;

@Plugin(id = "exampledataplugin", authors = "pie_flavor")
public class ExampleDataPlugin {
    @Inject
    PluginContainer container;
    @Inject
    Logger logger;

    @Listener
    public void preInit(GamePreInitializationEvent e) {
        MyKeys.dummy();
        DataRegistration.builder()
                .dataName("My Standard Data")
                .manipulatorId("standard_data") // prefix is added for you and you can't add it yourself
                .dataClass(MyStandardData.class)
                .immutableClass(MyImmutableStandardData.class)
                .builder(new MyStandardDataBuilder())
                .buildAndRegister(container);
        DataRegistration.builder()
                .dataName("My Singular Data")
                .manipulatorId("singular_data")
                .dataClass(MySingularData.class)
                .immutableClass(MySingularData.Immutable.class)
                .builder(new MySingularData.Builder())
                .buildAndRegister(container);
        DataRegistration.builder()
                .dataName("My Bool Data")
                .manipulatorId("bool_data")
                .dataClass(MyBoolData.class)
                .dataImplementation(MyBoolDataImpl.class)
                .immutableClass(MyBoolData.Immutable.class)
                .immutableImplementation(MyBoolDataImpl.Immutable.class)
                .builder(new MyBoolDataImpl.Builder())
                .buildAndRegister(container);
        Sponge.getDataManager().registerContentUpdater(MyBoolDataImpl.class, new MyBoolDataImpl.BoolEnabled1To2Updater());
    }

    @Listener
    public void onLogin(ClientConnectionEvent.Join ev, @Getter(value = "getTargetEntity") Player player) {
        player.getOrCreate(MyStandardData.class).ifPresent(myData -> {
            if (myData.amount().get() == 1) {
                player.offer(myData);
                player.offer(myData.amount().set(2));
                player.offer(myData.id().set(UUID.randomUUID()));
            }
        });

        player.getOrCreate(MyStandardData.class).ifPresent(myData -> {
            logger.info("DATA TEST (login): " + player.getName() + " = (amount=" + myData.amount().get() + ", id=" + myData.id().get() + ")");
        });
    }

    @Listener
    public void onLogout(ClientConnectionEvent.Disconnect ev, @Getter(value = "getTargetEntity") Player player) {
        player.getOrCreate(MyStandardData.class).ifPresent(myData -> {
            logger.info("DATA TEST (logout): " + player.getName() + " = (amount=" + myData.amount().get() + ", id=" + myData.id().get() + ")");
        });
    }

}
