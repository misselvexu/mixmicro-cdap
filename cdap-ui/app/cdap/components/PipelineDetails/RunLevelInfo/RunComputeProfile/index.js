/*
 * Copyright © 2018 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
*/

import React, {Component} from 'react';
import {connect} from 'react-redux';
import PropTypes from 'prop-types';
import {objectQuery, preventPropagation} from 'services/helpers';
import IconSVG from 'components/IconSVG';
import ProfilePreview from 'components/Cloud/Profiles/Preview';
import Popover from 'components/Popover';
import classnames from 'classnames';
import {extractProfileName} from 'components/Cloud/Profiles/Store/ActionCreator';
import {CLOUD} from 'services/global-constants';

require('./RunComputeProfile.scss');

class RunLevelComputeProfile extends Component {
  static propTypes = {
    profileName: PropTypes.string
  };

  render() {
    const ProfileLabel = () => {
      return (
        <div className={classnames("profile-preview-label", {
          'disabled': this.props.profileName === CLOUD.DEFAULT_PROFILE_NAME
        })}>
          {
          !this.props.profileName ?
            <button
              className="btn btn-link"
              title="Profile Information Unavailable"
              disabled
            >
              <IconSVG name="icon-cloud" />
              <span>--</span>
            </button>
          :
            <div
              onClick={(e) => {
                if (this.props.profileName === CLOUD.DEFAULT_PROFILE_NAME) {
                  preventPropagation(e);
                  return false;
                }
              }}
            >
              <IconSVG name="icon-cloud" />
              <span>{extractProfileName(this.props.profileName)}</span>
            </div>
          }
        </div>
      );
    };
    return (
      <div className="run-info-container run-level-compute-profile pull-right">
        <div>
          <strong> Run Compute Profile </strong>
        </div>
        {
          !this.props.profileName ?
            <ProfileLabel />
          :
            <Popover
              target={() => <ProfileLabel />}
              className="profile-preview-popover"
              placement="bottom-end"
              bubbleEvent={false}
              enableInteractionInPopover={true}
              injectOnToggle={true}
            >
              <ProfilePreview
                profileName={extractProfileName(this.props.profileName)}
                profileScope={this.props.profileName.indexOf('SYSTEM:') !== -1 ? 'system' : 'user'}
              />
            </Popover>
        }
      </div>
    );
  }
}

const getProfileName = (runProperties) => {
  let runtimeArgs;
  try {
    runtimeArgs = JSON.parse(runProperties);
  } catch (e) {
    return null;
  }
  return runtimeArgs[CLOUD.PROFILE_NAME_PREFERENCE_PROPERTY] || null;
};
const mapStateToProps = (state) => {
  return {
    profileName: getProfileName(objectQuery(state, 'currentRun', 'properties', 'runtimeArgs')) || CLOUD.DEFAULT_PROFILE_NAME
  };
};

const ConnectedRunLevelComputeProfile = connect(mapStateToProps)(RunLevelComputeProfile);

export default ConnectedRunLevelComputeProfile;
