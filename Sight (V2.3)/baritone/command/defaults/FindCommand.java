/*
 * This file is part of Baritone.
 *
 * Baritone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Baritone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Baritone.  If not, see <https://www.gnu.org/licenses/>.
 */

package baritone.command.defaults;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import baritone.api.IBaritone;
import baritone.api.command.Command;
import baritone.api.command.argument.IArgConsumer;
import baritone.api.command.datatypes.BlockById;
import baritone.api.command.exception.CommandException;
import baritone.api.utils.BetterBlockPos;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;

public class FindCommand extends Command {

    public FindCommand(IBaritone baritone) {
        super(baritone, "find");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        List<Block> toFind = new ArrayList<>();
        while (args.hasAny()) {
            toFind.add(args.getDatatypeFor(BlockById.INSTANCE));
        }
        for (Block block : toFind) {
            BetterBlockPos origin = ctx.playerFeet();
            ArrayList<BlockPos> poses = ctx.worldData().getCachedWorld().getLocationsOf(
                    Block.blockRegistry.getNameForObject(block).getResourcePath(),
                    Integer.MAX_VALUE,
                    origin.x,
                    origin.y,
                    4);
            
            ArrayList<BetterBlockPos> newPos = new ArrayList<BetterBlockPos>();
            for (BlockPos pos : poses) {
            	BetterBlockPos bbp = new BetterBlockPos(pos);
            	this.logDirect(pos.toString());
            }
        }
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) {
        return args.tabCompleteDatatype(BlockById.INSTANCE);
    }

    @Override
    public String getShortDesc() {
        return "Find positions of a certain block";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "",
                "",
                "Usage:",
                "> "
        );
    }
}
