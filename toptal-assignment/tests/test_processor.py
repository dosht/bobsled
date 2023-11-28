from applepen.processor import combine_data_frames
from pandas.testing import assert_frame_equal
from tests.fixtures import inventory, supply, sell, combined_data_frame


def test_combine_data_frames(inventory, supply, sell, combined_data_frame):
    result_df = combine_data_frames(inventory, supply, sell)
    assert_frame_equal(result_df, combined_data_frame)
